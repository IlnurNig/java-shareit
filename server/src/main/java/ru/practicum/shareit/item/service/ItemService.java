package ru.practicum.shareit.item.service;

import ru.practicum.shareit.exception.abstractClass.ExceptionBadRequest;
import ru.practicum.shareit.exception.abstractClass.ExceptionNotFound;
import ru.practicum.shareit.exception.iml.UnknownItemException;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, long userId) throws ExceptionNotFound, ExceptionBadRequest;

    ItemDto updateItem(ItemDto itemDto, long itemId, long userId) throws ExceptionNotFound;

    ItemDto getItemDtoById(long itemId, long userId) throws ExceptionNotFound;

    Collection<ItemDto> getAllItemDtoByIdUser(long userId);

    Collection<ItemDto> searchItemDtoByText(String text);

    void deleteItem(long itemId);

    Item getItemById(long itemId) throws UnknownItemException;

    CommentDto createComment(Long userId, CommentDto commentDto, Long itemId) throws ExceptionNotFound, ExceptionBadRequest;
}
