package com.sammy.codexhotel.dtos.responses;

import com.sammy.codexhotel.data.models.RoomType;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Numbering guidance for one category, so the admin room form can prefill the next free number and
 * name the band a number has to fall in — rather than leaving staff to learn the ranges and find
 * out from a rejected save. See RoomNumbering for the bands themselves.
 */
@Data
@AllArgsConstructor
public class RoomNumberingResponse {
    private RoomType roomType;

    /** Lowest unused number in the band, or null when every number in the band is taken. */
    private Integer nextNumber;

    /** Inclusive lower bound of the band; the form uses it as the number input's minimum. */
    private int bandStart;

    /** Human-readable band, e.g. "100-199" or "300 and above". */
    private String bandLabel;
}
