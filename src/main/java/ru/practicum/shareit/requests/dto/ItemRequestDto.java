package ru.practicum.shareit.requests.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * // TODO .
 */
@Data
@Builder
public class ItemRequestDto {
    private long id;

    private String description;

    private LocalDateTime created;

    private Set<ItemDto> items;
}
