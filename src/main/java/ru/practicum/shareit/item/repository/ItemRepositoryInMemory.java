package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

@Repository
public class ItemRepositoryInMemory implements ItemRepository {
    private static long countItem;
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item createItem(Item item) {
        item.setItemId(++countItem);
        items.put(item.getItemId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        items.put(item.getItemId(), item);
        return item;
    }

    @Override
    public Optional<Item> getItemById(long itemId) {
        if (items.containsKey(itemId)) {
            return Optional.of(items.get(itemId));
        }
        return Optional.empty();
    }

    @Override
    public Collection<Item> getAllItemByIdUser(long userId) {
        return items.values().stream()
                .filter(a -> a.getUserId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Item> searchItemByIdUserAndByText(long userId, String text) {
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(a -> StringUtils.containsIgnoreCase(a.getDescription(), text) ||
                        StringUtils.containsIgnoreCase(a.getName(), text))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItem(long itemId) {
        items.remove(itemId);
    }
}
