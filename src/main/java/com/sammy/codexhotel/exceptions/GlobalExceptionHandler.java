package com.sammy.codexhotel.exceptions;

import com.sammy.codexhotel.dtos.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Renders request-validation failures and domain rejections in the same {@link ApiResponse} envelope
 * the controllers use.
 *
 * <p>Without this, Spring falls back to {@code response.sendError}, which forwards to /error in a
 * separate ERROR dispatch — a different response shape from every other endpoint, and one the
 * frontend cannot show field-level messages from.
 *
 * <p>Deliberately does not handle {@code AccessDeniedException} or {@code AuthenticationException}:
 * those must keep propagating to Spring Security's ExceptionTranslationFilter so the 401/403
 * handlers stay in charge of them.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationErrors(MethodArgumentNotValidException exception) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            fieldErrors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(false, "Please correct the highlighted fields", fieldErrors));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse> handleUnreadableBody(HttpMessageNotReadableException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(false, "Request body could not be read; check the field types", null));
    }

    /**
     * A room number outside the band its category is numbered in. Reported in the same shape as a
     * bean-validation failure — generic message, specifics keyed by field — so the admin room form
     * marks the offending input instead of only showing a banner. Pinned to roomNumber even when a
     * category change is what pushed the pair out of range: that is the field the admin would edit,
     * and the message names both ways out.
     */
    @ExceptionHandler(RoomNumberOutOfRangeException.class)
    public ResponseEntity<ApiResponse> handleRoomNumberOutOfRange(RoomNumberOutOfRangeException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(false, "Please correct the highlighted fields",
                        Map.of("roomNumber", exception.getMessage())));
    }

    /**
     * A room whose status cannot move because an active reservation holds it. 409 rather than 400:
     * the request is well-formed and would be valid at another time — it is the current state that
     * refuses it.
     */
    @ExceptionHandler(RoomUnavailableException.class)
    public ResponseEntity<ApiResponse> handleRoomUnavailable(RoomUnavailableException exception) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiResponse(false, exception.getMessage(), null));
    }
}
