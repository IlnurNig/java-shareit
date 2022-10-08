package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Getter
@Builder
public class BookingOutputDto {

    private Long id;

    @JsonProperty("start")
    private LocalDateTime startTime;

    @JsonProperty("end")
    private LocalDateTime endTime;

    private ItemDto item;

    private UserDto booker;

    private String status;

    private long bookerId;
}