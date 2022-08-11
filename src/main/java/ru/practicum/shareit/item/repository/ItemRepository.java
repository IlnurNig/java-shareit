package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {
    Item createItem(Item item);

    Item updateItem(Item item);

    Optional<Item> getItemById(long itemId);

    Collection<Item> getAllItemByIdUser(long userId);

    Collection<Item> searchItemByIdUserAndByText(long userId, String text);

    void deleteItem(long itemId);

}
