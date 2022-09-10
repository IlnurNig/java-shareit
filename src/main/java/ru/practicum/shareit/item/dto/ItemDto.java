package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.item.comment.CommentDto;

import java.util.Set;

/**
 * // TODO .
 */
@Data
@Builder
public class ItemDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingOutputDto lastBooking;
    private BookingOutputDto nextBooking;
    private Set<CommentDto> comments;
}
