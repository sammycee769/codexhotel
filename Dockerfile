# CodexHotel API — build and runtime image.
#
#   docker build -t codexhotel-api .
#   docker run --rm -p 8080:8080 \
#     -e SPRING_MONGODB_URI=mongodb://host.docker.internal:27017/codexhotel \
#     -e CODEXHOTEL_JWT_SECRET=<at least 32 bytes> \
#     codexhotel-api
#
# SPRING_MONGODB_URI is effectively required. The default in application.properties is
# mongodb://localhost:27017/codexhotel, and inside a container localhost is the container itself,
# so the app would start and then fail on the first query. Note the Boot 4 property name — the
# spring.data.mongodb.* prefix was removed, so SPRING_DATA_MONGODB_URI is silently ignored.
#
# Every codexhotel.* property is overridable the same way (uppercase, dots to underscores,
# dashes dropped):
#   CODEXHOTEL_JWT_SECRET              HS256 signing key, 32+ bytes. Override always: the
#                                      committed default is a published dev value.
#   CODEXHOTEL_CORS_ALLOWEDORIGINS     comma-separated frontend origins
#   CODEXHOTEL_ADMIN_EMAIL / _PASSWORD / _NAME / _PHONENUMBER
#                                      the first-run administrator, seeded only when no ADMIN exists
#   CODEXHOTEL_ROOMS_SINGLECOUNT / _DOUBLECOUNT / _SUITECOUNT
#                                      inventory seeded only when the rooms collection is empty

# ---------------------------------------------------------------- build
# The Maven wrapper rather than a maven:* image, so the container builds with the same
# Maven 3.9.14 the project pins and there is one fewer version to keep in step.
FROM eclipse-temurin:21-jdk AS build

WORKDIR /build

# Wrapper first and on its own: distributionType=only-script means mvnw downloads Maven on first
# use, and that download is worth caching independently of the dependency resolution below.
COPY .mvn/ .mvn/
COPY mvnw ./
RUN chmod +x mvnw && ./mvnw -B -ntp --version

# Dependencies resolve from the POM alone, so this layer survives every source-only change.
COPY pom.xml ./
RUN ./mvnw -B -ntp dependency:go-offline

COPY src/ src/
# Tests are the CI gate, not the image build: they would need a MongoDB to talk to and would
# make every image rebuild pay for a test run.
RUN ./mvnw -B -ntp -DskipTests package

# Split the uber jar into its layers so the runtime stage can order them by how often they
# change — third-party dependencies are a stable ~35MB, application classes are not.
RUN java -Djarmode=tools -jar target/codexhotel-*.jar \
        extract --layers --launcher --destination /build/layers

# ---------------------------------------------------------------- runtime
# JRE, not JDK: nothing at runtime compiles, and it drops the image by several hundred MB.
FROM eclipse-temurin:21-jre AS runtime

# LocalDate.now() decides which reservations the checkout scheduler treats as expired, and
# @Future rejects a check-in date that is not ahead of it — both read the JVM's zone. Left at UTC
# so behaviour is reproducible; set TZ to the hotel's own zone if local dates are what matter.
ENV TZ=UTC

# Unprivileged: the app neither writes to disk nor binds a privileged port.
RUN useradd --system --create-home --uid 10001 --shell /usr/sbin/nologin app
WORKDIR /app

# One COPY per layer, least- to most-frequently-changed, so a code edit invalidates only the last.
COPY --from=build --chown=app:app /build/layers/dependencies/ ./
COPY --from=build --chown=app:app /build/layers/spring-boot-loader/ ./
COPY --from=build --chown=app:app /build/layers/snapshot-dependencies/ ./
COPY --from=build --chown=app:app /build/layers/application/ ./

USER app
EXPOSE 8080

# Public and backed by a Mongo query, so a pass means the app is up *and* can reach its database.
# start-period is generous and start-interval probes inside it every few seconds: cold starts on a
# loaded machine have been seen to take three minutes, and without this the container is reported
# unhealthy on the way up — enough to fail a `depends_on: condition: service_healthy`.
#HEALTHCHECK --start-period=240s --start-interval=5s --interval=30s --timeout=5s --retries=3 \
#    CMD curl -fsS http://localhost:8080/api/rooms/available > /dev/null || exit 1

# MaxRAMPercentage because the JVM's default quarter-of-the-container is wasteful when the
# container limit *is* the app's budget; ExitOnOutOfMemoryError so an exhausted heap becomes a
# restart rather than a process that lingers on half-serving requests.
ENTRYPOINT ["java", \
    "-XX:MaxRAMPercentage=75.0", \
    "-XX:+ExitOnOutOfMemoryError", \
    "org.springframework.boot.loader.launch.JarLauncher"]
