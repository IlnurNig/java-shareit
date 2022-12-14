package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * // TODO .
 */
@Getter
@Builder
public class BookingDto {
    private long id;

    @JsonProperty("start")
    private LocalDateTime start;

    @JsonProperty("end")
    private LocalDateTime end;

    private Long itemId;

    private Long bookerId;

    private String status;
}
