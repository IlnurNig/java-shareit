package ru.practicum.shareit.requests.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exception.abstractClass.ExceptionBadRequest;
import ru.practicum.shareit.exception.abstractClass.ExceptionNotFound;
import ru.practicum.shareit.exception.iml.UnknownItemException;
import ru.practicum.shareit.exception.iml.ValidationException;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestMapper;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;
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
            throws ExceptionNotFound, ExceptionBadRequest {
        validateCreateRequest(itemRequestDto);
        User user = userService.getUserById(userId);
        ItemRequest itemRequest = ItemRequestMapper.toEntity(itemRequestDto);
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());
        return ItemRequestMapper.toDto(requestRepository.save(itemRequest));
    }

    public ItemRequestDto getRequestDtoById(Long userId, long requestId) throws ExceptionNotFound {
        User user = userService.getUserById(userId);
        try {
            return ItemRequestMapper.toDto(getRequestById(requestId));
        } catch (EntityNotFoundException e) {
            log.info("The Request with id={} does not exist", requestId);
            throw new UnknownItemException(String.format("The Request with id=%d does not exist", requestId));
        }

    }

    public ItemRequest getRequestById(long requestId) {
        return requestRepository.getReferenceById(requestId);
    }

    public Collection<ItemRequestDto> getAllRequestByUserId(long userId) throws ExceptionNotFound {
        User user = userService.getUserById(userId);
        return ItemRequestMapper.toDto(requestRepository.findAlLByRequester_IdOrderByCreatedDesc(userId));
    }

    private void validateCreateRequest(ItemRequestDto itemRequestDto) throws ExceptionBadRequest {
        if (!StringUtils.hasText(itemRequestDto.getDescription())) {
            log.info("Add empty description failed");
            throw new ValidationException("Add empty description failed");
        }
    }

    public Collection<ItemRequestDto> getAllRequest(Long userId, Integer from, Integer size)
            throws ExceptionNotFound, ExceptionBadRequest {
        User user = userService.getUserById(userId);
        validateFromAndSize(from, size);
        Pageable pageable = PageRequest.of(
                Objects.requireNonNullElse(from, 0),
                Objects.requireNonNullElse(size, Integer.MAX_VALUE),
                Sort.by("created").descending());
        return requestRepository.findAll(pageable).stream()
                .filter(a -> a.getRequester().getId() != userId)
                .map(ItemRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    private void validateFromAndSize(Integer from, Integer size) throws ValidationException {
        if (Objects.requireNonNullElse(from, 0) < 0) {
            log.info("incorrect from={}", from);
            throw new ValidationException(String.format("incorrect from=%d", from));
        }
        if (Objects.requireNonNullElse(size, 0) < 0) {
            log.info("incorrect size={}", size);
            throw new ValidationException(String.format("incorrect size=%d", size));
        }
    }
}
