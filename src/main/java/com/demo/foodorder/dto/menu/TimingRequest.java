package com.demo.foodorder.dto.menu;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;

@Getter
@Setter
public class TimingRequest {

    /**
     * Day name e.g. MONDAY, TUESDAY
     */
    @NotNull
    private DayOfWeek dayOfWeek;

    /**
     * 24h format HH:mm, e.g. 09:00
     */
    @NotNull
    @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d)$", message = "openTime must be HH:mm")
    private String openTime;

    @NotNull
    @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d)$", message = "closeTime must be HH:mm")
    private String closeTime;
}
