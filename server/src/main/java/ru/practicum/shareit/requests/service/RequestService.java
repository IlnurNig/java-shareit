package ru.practicum.shareit.requests.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.abstractClass.ExceptionNotFound;
import ru.practicum.shareit.exception.iml.UnknownItemException;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestMapper;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RequestService {

    private final RequestRepository requestRepository;
    private final UserService userService;

    @Autowired
    public RequestService(RequestRepository requestRepository, UserService userService) {
        this.requestRepository = requestRepository;
        this.userService = userService;
    }

    public ItemRequestDto createRequest(Long userId, ItemRequestDto itemRequestDto)
            throws ExceptionNotFound {
        User user = userService.getUserById(userId);
        ItemRequest itemRequest = ItemRequestMapper.toEntity(itemRequestDto);
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());
        return ItemRequestMapper.toDto(requestRepository.save(itemRequest));
    }

    public ItemRequestDto getRequestDtoById(Long userId, long requestId) throws ExceptionNotFound {
        User user = userService.getUserById(userId);
        try {
            log.info("getRequestDtoById {}", requestId);
            return ItemRequestMapper.toDto(getRequestById(requestId));
        } catch (EntityNotFoundException e) {
            throw new UnknownItemException(String.format("The Request with id=%d does not exist", requestId));
        }

    }

    public ItemRequest getRequestById(long requestId) {
        log.info("getRequestById {}", requestId);
        return requestRepository.getReferenceById(requestId);
    }

    public Collection<ItemRequestDto> getAllRequestByUserId(long userId) throws ExceptionNotFound {
        User user = userService.getUserById(userId);
        log.info("getAllRequestByUserId {}", userId);
        return ItemRequestMapper.toDto(requestRepository.findAlLByRequester_IdOrderByCreatedDesc(userId));
    }

    public Collection<ItemRequestDto> getAllRequest(Long userId, Integer from, Integer size)
            throws ExceptionNotFound {
        User user = userService.getUserById(userId);
        Pageable pageable = PageRequest.of(
                from,
                size,
                Sort.by("created").descending());
        log.info("getAllRequest userId:{}, from:{}, size:{}", userId, from, size);
        return requestRepository.findAll(pageable).stream()
                .filter(a -> a.getRequester().getId() != userId)
                .map(ItemRequestMapper::toDto)
                .collect(Collectors.toList());
    }

}
