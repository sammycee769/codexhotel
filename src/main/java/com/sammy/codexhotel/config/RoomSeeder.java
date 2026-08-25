package com.sammy.codexhotel.config;

import com.sammy.codexhotel.data.models.Room;
import com.sammy.codexhotel.data.models.RoomStatus;
import com.sammy.codexhotel.data.models.RoomType;
import com.sammy.codexhotel.data.repositories.RoomRepo;
import com.sammy.codexhotel.services.PricingService;
import com.sammy.codexhotel.utils.RoomNumbering;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Stocks the hotel on first startup, because an empty inventory makes the whole booking flow
 * untestable: a category with one room accepts one stay and then reports itself full, and the
 * banded numbering has nothing to number against.
 *
 * <p>Numbers come from {@link RoomNumbering#firstRoomNumber} upward, contiguously within each
 * band — 101, 102, 103… for Deluxe, 201… for Junior, 301… for Executive — so the numbers a
 * seeded hotel starts with are the same ones the admin form would have offered one at a time.
 *
 * <p>Runs only when the rooms collection is empty, which is the same contract {@link AdminSeeder}
 * uses: a live inventory is never touched, restarts never duplicate, and a count changed in the
 * properties file will not retroactively add or remove rooms. Emptying the collection and
 * restarting re-stocks it, which is the intended way to reset a development database.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RoomSeeder {

    private final RoomRepo roomRepository;
    private final PricingService pricingService;

    @Value("${codexhotel.rooms.single-count}")
    private int singleCount;

    @Value("${codexhotel.rooms.double-count}")
    private int doubleCount;

    @Value("${codexhotel.rooms.suite-count}")
    private int suiteCount;

    @EventListener(ApplicationReadyEvent.class)
    public void seedRooms() {
        long existing = roomRepository.count();
        if (existing > 0) {
            log.info("Inventory already stocked ({} rooms), skipping seed", existing);
            return;
        }

        Map<RoomType, Integer> counts = new LinkedHashMap<>();
        counts.put(RoomType.SINGLE, singleCount);
        counts.put(RoomType.DOUBLE, doubleCount);
        counts.put(RoomType.SUITE, suiteCount);

        List<Room> stock = new ArrayList<>();
        counts.forEach((type, requested) -> stock.addAll(buildBand(type, requested)));

        if (stock.isEmpty()) {
            log.warn("Every room count is zero or negative; inventory left empty");
            return;
        }

        roomRepository.saveAll(stock);
        counts.forEach((type, requested) -> {
            if (requested > 0) {
                log.info("Seeded {} {} rooms, numbered from {}",
                        Math.min(requested, bandCapacity(type)), type, RoomNumbering.firstRoomNumber(type));
            }
        });
    }

    private List<Room> buildBand(RoomType type, int requested) {
        if (requested <= 0) {
            log.warn("Room count for {} is {}; seeding none of that category", type, requested);
            return List.of();
        }

        // A count larger than the band cannot be honoured without numbering rooms outside it,
        // which the API would then reject on every edit. Clamped rather than thrown so a bad
        // property value does not stop the application from starting.
        int capacity = bandCapacity(type);
        int count = Math.min(requested, capacity);
        if (count < requested) {
            log.warn("Room count for {} is {} but the {} band only holds {}; seeding {}",
                    type, requested, RoomNumbering.bandLabel(type), capacity, count);
        }

        double price = pricingService.getPrices(type);
        List<Room> band = new ArrayList<>(count);
        for (int offset = 0; offset < count; offset++) {
            int number = RoomNumbering.firstRoomNumber(type) + offset;
            // Cannot fail after the clamp above; kept so the seeder is held to the same policy
            // as the API rather than trusted to agree with it.
            RoomNumbering.validate(type, number);

            Room room = new Room();
            room.setRoomNumber(number);
            room.setRoomType(type);
            room.setRoomStatus(RoomStatus.AVAILABLE);
            // The tariff PricingService actually charges, so a seeded room's stored price is not
            // immediately contradicted by what a guest pays.
            room.setPricePerNight(price);
            band.add(room);
        }
        return band;
    }

    /** How many rooms fit between the band's first usable number and its ceiling. */
    private static int bandCapacity(RoomType type) {
        long span = (long) RoomNumbering.lastNumberInBand(type) - RoomNumbering.firstRoomNumber(type) + 1;
        return (int) Math.min(span, Integer.MAX_VALUE);
    }
}
