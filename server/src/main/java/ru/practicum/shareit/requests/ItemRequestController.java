package ru.practicum.shareit.requests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.abstractClass.ExceptionBadRequest;
import ru.practicum.shareit.exception.abstractClass.ExceptionNotFound;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.service.RequestService;

import java.util.Collection;


@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final RequestService requestService;

    @Autowired
    public ItemRequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestBody ItemRequestDto itemRequestDto)
            throws ExceptionNotFound, ExceptionBadRequest {
        return requestService.createRequest(userId, itemRequestDto);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestDtoById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @PathVariable long requestId)
            throws ExceptionNotFound {
        return requestService.getRequestDtoById(userId, requestId);
    }

    @GetMapping
    public Collection<ItemRequestDto> getAllRequestByUserId(@RequestHeader("X-Sharer-User-Id") Long userId)
            throws ExceptionNotFound {
        return requestService.getAllRequestByUserId(userId);
    }

    @GetMapping("all")
    public Collection<ItemRequestDto> getAllRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam(name = "from") Integer from,
                                                    @RequestParam(name = "size") Integer size)
            throws ExceptionNotFound, ExceptionBadRequest {
        return requestService.getAllRequest(userId, from, size);
    }


}
