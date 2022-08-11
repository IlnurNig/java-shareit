package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.abstractClass.ExceptionBadRequest;
import ru.practicum.shareit.exception.abstractClass.ExceptionNotFound;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

/**
 * // TODO .
 */
@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto createItem(@RequestBody ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") long userId)
            throws ExceptionNotFound, ExceptionBadRequest {
        log.info("POST /items {}", itemDto);
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @PathVariable long itemId,
                              @RequestHeader("X-Sharer-User-Id") long userId)
            throws ExceptionNotFound {
        log.info("PATCH /items/{}", itemId);
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemDtoById(@PathVariable long itemId) throws ExceptionNotFound {
        log.info("GET /items/{}", itemId);
        return itemService.getItemDtoById(itemId);
    }

    @GetMapping
    public Collection<ItemDto> getAllItemDtoByIdUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("GET /items X-Sharer-User-Id:{}", userId);
        return itemService.getAllItemDtoByIdUser(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItemDtoByIdUserAndByText(@RequestHeader("X-Sharer-User-Id") long userId,
                                                              @RequestParam(value = "text") String text) {
        log.info("GET /items/search X-Sharer-User-Id:{}, text:{}", userId, text);
        return itemService.searchItemDtoByIdUserAndByText(userId, text);
    }

}
