package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;


    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                                @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestClient.createRequest(userId, itemRequestDto);
    }


    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestDtoById(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                                    @PathVariable long requestId) {
        return itemRequestClient.getRequestDtoById(userId, requestId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequestByUserId(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        return itemRequestClient.getAllRequestByUserId(userId);
    }

    @GetMapping("all")
    public ResponseEntity<Object> getAllRequest
            (@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
             @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
             @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemRequestClient.getAllRequest(userId, from, size);
    }

}
