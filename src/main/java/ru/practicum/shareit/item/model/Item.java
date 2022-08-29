package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;

/**
 * // TODO .
 */
@Data
@Builder
public class Item {
    private long userId;
    private long itemId;
    private String name;
    private String description;
    private Boolean available;
}
