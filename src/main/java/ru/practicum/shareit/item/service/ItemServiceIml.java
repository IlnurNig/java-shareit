package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.status.Status;
import ru.practicum.shareit.exception.abstractClass.ExceptionBadRequest;
import ru.practicum.shareit.exception.abstractClass.ExceptionNotFound;
import ru.practicum.shareit.exception.iml.UnknownItemException;
import ru.practicum.shareit.exception.iml.UnknownUserException;
import ru.practicum.shareit.exception.iml.ValidationException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemServiceIml implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final CommentRepository commentRepository;

    @Autowired
    public ItemServiceIml(ItemRepository itemRepository,
                          UserService userService,
                          CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.commentRepository = commentRepository;
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, long userId) throws ExceptionNotFound, ExceptionBadRequest {
        Item item = ItemMapper.toEntity(itemDto);
        validateCreateItem(item, userId);
        User user = UserMapper.toEntity(userService.getUserDtoById(userId));
        item.setUser(user);
        return ItemMapper.toDto(itemRepository.save(item));
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
        return ItemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getItemDtoById(long itemId, long userId) throws ExceptionNotFound {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new UnknownItemException(String.format("The item with id=%d does not exist", itemId)));
        if (item.getUser().getId() == userId) {
            addLastAndNextBooking(item);
            return ItemMapper.toDtoOwner(item);
        }
        return ItemMapper.toDto(item);
    }

    @Override
    public Collection<ItemDto> getAllItemDtoByIdUser(long userId) {
        return itemRepository.findAllByUserId(userId).stream()
                .map(ItemServiceIml::addLastAndNextBooking)
                .map(ItemMapper::toDtoOwner)
                .sorted(Comparator.comparing(ItemDto::getId))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> searchItemDtoByText(String text) {
        if (!StringUtils.hasText(text)) {
            return new ArrayList<>();
        }
        return itemRepository.search(text).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItem(long itemId) {
        itemRepository.deleteById(itemId);
    }

    @Override
    public Item getItemById(long itemId) throws UnknownItemException {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new UnknownItemException(String.format("The item with id=%d does not exist", itemId)));
    }

    @Override
    public CommentDto createComment(Long userId, CommentDto commentDto, Long itemId)
            throws ExceptionNotFound, ExceptionBadRequest {
        User author = userService.getUserById(userId);
        Item item = getItemById(itemId);
        validateCreateComment(item, author, commentDto);
        Comment comment = CommentMapper.toEntity(commentDto, item, author);

        return CommentMapper.toDto(commentRepository.save(comment));
    }

    private void validateCreateComment(Item item, User author, CommentDto commentDto) throws ExceptionBadRequest {
        item.getBookings().stream()
                .filter(a -> a.getBooker().getId() == author.getId())
                .filter(a -> a.getStatus().equals(Status.APPROVED) || a.getStatus().equals(Status.CANCELED))
                .filter(a -> a.getStart().isBefore(LocalDateTime.now()))
                .findFirst()
                .orElseThrow(
                        () -> new ValidationException(
                                String.format("Add comment to item id=%d without booking failed", item.getItemId()))
                );
        if (!StringUtils.hasText(commentDto.getText())) {
            throw new ValidationException("Add empty comment failed");
        }
    }

    private void validateCreateItem(Item item, long userId) throws UnknownUserException, ValidationException {
        if (!userService.containsId(userId)) {
            throw new UnknownUserException(String.format("The user with id=%d does not exist", userId));
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
        if (!(item.getUser().getId() == userId)) {
            throw new UnknownItemException(String.format("The item with id=%d must be equal to id=%d",
                    item.getUser().getId(), userId));
        }
    }

    private static Item addLastAndNextBooking(Item item) {
        item.setLastBooking(item.getBookings().stream()
                .sorted(Comparator.comparing(Booking::getEnd).reversed())
                .filter(booking -> LocalDateTime.now().isAfter(booking.getEnd()))
                .findFirst()
                .orElse(null)
        );
        item.setNextBooking(item.getBookings().stream()
                .sorted(Comparator.comparing(Booking::getStart))
                .filter(booking -> LocalDateTime.now().isBefore(booking.getStart()))
                .findFirst()
                .orElse(null)
        );
        return item;
    }
}
