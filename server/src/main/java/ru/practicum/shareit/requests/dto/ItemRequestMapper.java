package ru.practicum.shareit.requests.dto;

import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.requests.model.ItemRequest;

import java.util.Collection;
import java.util.stream.Collectors;

public class ItemRequestMapper {
    public static ItemRequest toEntity(ItemRequestDto itemRequestDto) {
        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .created(itemRequestDto.getCreated())
                .build();
    }

    public static ItemRequestDto toDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(ItemMapper.toDto(itemRequest.getItems()))
                .build();
    }

    public static Collection<ItemRequestDto> toDto(Collection<ItemRequest> itemRequests) {
        if (itemRequests == null) return null;
        return itemRequests.stream().map(ItemRequestMapper::toDto).collect(Collectors.toList());
    }
}
