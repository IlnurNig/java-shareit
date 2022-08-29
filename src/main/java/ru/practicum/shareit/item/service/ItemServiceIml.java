package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exception.abstractClass.ExceptionBadRequest;
import ru.practicum.shareit.exception.abstractClass.ExceptionNotFound;
import ru.practicum.shareit.exception.iml.UnknownItemException;
import ru.practicum.shareit.exception.iml.UnknownUserException;
import ru.practicum.shareit.exception.iml.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemServiceIml implements ItemService {
    private final ItemRepository repository;
    private final ItemMapper mapper;
    private final UserService userService;

    @Autowired
    public ItemServiceIml(ItemRepository repository, ItemMapper mapper, UserService userService) {
        this.repository = repository;
        this.mapper = mapper;
        this.userService = userService;
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, long userId) throws ExceptionNotFound, ExceptionBadRequest {
        Item item = mapper.toEntity(itemDto);
        item.setUserId(userId);
        validateCreateItem(item);
        return mapper.toDto(repository.createItem(item));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long itemId, long userId) throws ExceptionNotFound {
        Item item = getItemById(itemId);
        validationUpdateItem(itemDto, item, userId);
        if (!(itemDto.getAvailable() == null)) {
            item.setAvailable(itemDto.getAvailable());
        }
        if (!(itemDto.getName() == null)) {
            item.setName(itemDto.getName());
        }
        if (!(itemDto.getDescription() == null)) {
            item.setDescription(itemDto.getDescription());
        }
        return mapper.toDto(item);
    }

    @Override
    public ItemDto getItemDtoById(long itemId) throws ExceptionNotFound {
        Item item = repository.getItemById(itemId).orElseThrow(() ->
                new UnknownItemException(String.format("The item with id=%d does not exist", itemId)));
        return mapper.toDto(item);
    }

    @Override
    public Collection<ItemDto> getAllItemDtoByIdUser(long userId) {
        return repository.getAllItemByIdUser(userId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> searchItemDtoByIdUserAndByText(long userId, String text) {
        if (!StringUtils.hasText(text)) {
            return new ArrayList<>();
        }
        return repository.searchItemByIdUserAndByText(userId, text).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItem(long itemId) {
        repository.deleteItem(itemId);
    }

    private Item getItemById(long itemId) throws UnknownItemException {
        return repository.getItemById(itemId).orElseThrow(() ->
                new UnknownItemException(String.format("The item with id=%d does not exist", itemId)));
    }

    private void validateCreateItem(Item item) throws UnknownUserException, ValidationException {
        if (!userService.containsId(item.getUserId())) {
            throw new UnknownUserException(String.format("The user with id=%d does not exist", item.getUserId()));
        }
        if (item.getAvailable() == null) {
            throw new ValidationException("The available cannot be empty");
        }
        if (!StringUtils.hasText(item.getName())) {
            throw new ValidationException("The name cannot be empty");
        }
        if (!StringUtils.hasText(item.getDescription())) {
            throw new ValidationException("The description cannot be empty");
        }
    }

    private void validationUpdateItem(ItemDto itemDto, Item item, long userId) throws UnknownItemException {
        if (!(item.getUserId() == userId)) {
            throw new UnknownItemException(String.format("The item with id=%d must be equal to id=%d",
                    item.getUserId(), userId));
        }
    }
}
