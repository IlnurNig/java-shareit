package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemMapper {
    public Item toEntity(ItemDto dto) {
        return Item.builder()
                .itemId(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .build();
    }

    public ItemDto toDto(Item item) {
        return ItemDto.builder()
                .id(item.getItemId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }
}
