package ru.practicum.shareit.item.service;

import ru.practicum.shareit.exception.abstractClass.ExceptionBadRequest;
import ru.practicum.shareit.exception.abstractClass.ExceptionNotFound;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, long userId) throws ExceptionNotFound, ExceptionBadRequest;

    ItemDto updateItem(ItemDto itemDto, long itemId, long userId) throws ExceptionNotFound;

    ItemDto getItemDtoById(long itemId) throws ExceptionNotFound;

    Collection<ItemDto> getAllItemDtoByIdUser(long userId);

    Collection<ItemDto> searchItemDtoByIdUserAndByText(long userId, String text);

    void deleteItem(long itemId);
}
