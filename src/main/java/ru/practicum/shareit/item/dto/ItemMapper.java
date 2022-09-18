package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ItemMapper {
    public static Item toEntity(ItemDto dto) {
        Item item = new Item();
        item.setItemId(dto.getId());
        item.setDescription(dto.getDescription());
        item.setName(dto.getName());
        item.setAvailable(dto.getAvailable());
        return item;
    }

    public static ItemDto toDto(Item item) {
        ItemDto itemDto = ItemDto.builder()
                .id(item.getItemId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .comments(CommentMapper.toDto(item.getComments()))
                .build();
        if (item.getItemRequest() != null) {
            itemDto.setRequestId(item.getItemRequest().getId());
        }
        return itemDto;
    }

    public static Set<ItemDto> toDto(Set<Item> items) {
        if (items == null) return null;
        return items.stream().map(ItemMapper::toDto).collect(Collectors.toSet());
    }

    public static ItemDto toDtoOwner(Item item) {
        ItemDto itemDto = toDto(item);
        if (item.getLastBooking() != null) {
            itemDto.setLastBooking(BookingMapper.toDto(item.getLastBooking()));
        }
        if (item.getNextBooking() != null) {
            itemDto.setNextBooking(BookingMapper.toDto(item.getNextBooking()));
            BookingMapper.toDto(item.getNextBooking());
        }
        return itemDto;
    }
}
