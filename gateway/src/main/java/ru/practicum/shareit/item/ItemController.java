package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;


@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@Valid @RequestBody ItemDto itemDto,
                                             @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("POST /items {}", itemDto);
        return itemClient.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestBody ItemDto itemDto,
                                             @PathVariable long itemId,
                                             @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("PATCH /items/{}", itemId);
        return itemClient.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemDtoById(@PathVariable long itemId,
                                                 @RequestHeader("X-Sharer-User-Id") @NotNull long userId) {
        log.info("GET /items/{}", itemId);
        return itemClient.getItemDtoById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemDtoByIdUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("GET /items X-Sharer-User-Id:{}", userId);
        return itemClient.getAllItemDtoByIdUser(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemDtoByIdUserAndByText(@RequestHeader("X-Sharer-User-Id") long userId,
                                                                 @RequestParam(value = "text") String text) {
        log.info("GET /items/search X-Sharer-User-Id:{}, text:{}", userId, text);
        return itemClient.searchItemDtoByIdUserAndByText(userId, text);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                                @Valid @RequestBody CommentDto commentDto,
                                                @PathVariable @NotNull Long itemId) {
        return itemClient.createComment(userId, commentDto, itemId);
    }

}
