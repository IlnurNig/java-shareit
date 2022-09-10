package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.repository.BookingRepository;
import ru.practicum.shareit.booking.model.status.Status;
import ru.practicum.shareit.exception.abstractClass.ExceptionBadRequest;
import ru.practicum.shareit.exception.abstractClass.ExceptionNotFound;
import ru.practicum.shareit.exception.iml.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Autowired
    public BookingService(BookingRepository bookingRepository,
                          UserService userService, ItemService itemService) {
        this.bookingRepository = bookingRepository;
        this.userService = userService;
        this.itemService = itemService;
    }

    public BookingOutputDto createBookingDto(BookingDto bookingDto, long bookerId) throws ExceptionNotFound,
            ValidationException {
        User booker = userService.getUserById(bookerId);
        Item item = itemService.getItemById(bookingDto.getItemId());
        validateCreateBooking(item, bookingDto, booker);

        Booking booking = BookingMapper.toEntity(bookingDto);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        return BookingMapper.toDto(bookingRepository.save(booking));
    }

    public BookingOutputDto approve(long userId, long bookingId, boolean approved)
            throws ExceptionNotFound, ExceptionBadRequest {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new UnknownBookingException(String.format("The booking with id=%d does not exist", bookingId)));
        if (userId != booking.getItem().getUser().getId()) {
            throw new UnknownUserException(String.format("the user with id=%d is not the owner of the thing", userId));
        }
        if (booking.getStatus() == Status.APPROVED) {
            throw new ValidationException(String.format("Booking with id=%d previously approved", bookingId));
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return BookingMapper.toDto(bookingRepository.save(booking));
    }

    public BookingOutputDto getBookingByBookingIdAndUserId(long bookingId, Long userId)
            throws ExceptionNotFound {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new UnknownBookingException(String.format("The booking with id=%d does not exist", bookingId)));
        if (userId != booking.getBooker().getId() && userId != booking.getItem().getUser().getId()) {
            throw new UnknownUserException(String.format("user with id=%d has no access rights", userId));
        }
        return BookingMapper.toDto(booking);
    }

    public Collection<BookingOutputDto> getAllBookingByUserIdAndState(Long userId, String state)
            throws ExceptionInteralServerError {

        State st = getState(state);

        if (!userService.containsId(userId)) {
            throw new ExceptionInteralServerError("wrong user");
        }

        List<Booking> bookings = bookingRepository.findBookingByBookerIdOrderByStartDesc(userId);

        return filterBookingsByState(st, bookings).stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }


    public Collection<BookingOutputDto> getAllBookingByOwnerIdAndState(Long userId, String state)
            throws ExceptionInteralServerError {

        State st = getState(state);

        if (!userService.containsId(userId)) {
            throw new ExceptionInteralServerError("wrong user");
        }

        List<Booking> bookings = bookingRepository.findBookingByItem_User_IdOrderByStartDesc(userId);

        return filterBookingsByState(st, bookings).stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }

    private State getState(String st) throws ExceptionInteralServerError {
        try {
            return State.valueOf(st);
        } catch (IllegalArgumentException e) {
            throw new ExceptionInteralServerError("Unknown state: " + st);
        }
    }

    private void validateCreateBooking(Item item, BookingDto bookingDto, User booker)
            throws ValidationException, ExceptionNotFound {
        if (!item.getAvailable()) {
            throw new ValidationException(String.format("the item with id=%d is occupied", item.getItemId()));
        }
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            throw new ValidationException("the start date is later than the end");
        }
        if (bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("start date earlier than the current date");
        }
        if (item.getUser().getId() == booker.getId()) {
            throw new UnknownBookingException(
                    String.format("the owner with id=%d cannot create a request", item.getItemId()));
        }
    }

    private List<Booking> filterBookingsByState(State state, List<Booking> bookings)
            throws ExceptionInteralServerError {
        switch (state) {
            case ALL:
                return bookings;
            case WAITING:
            case REJECTED:
                Status status = Status.valueOf(state.toString());
                return bookings.stream()
                        .filter(booking -> booking.getStatus().equals(status))
                        .collect(Collectors.toList());
            case PAST:
                return bookings.stream()
                        .filter(booking -> LocalDateTime.now().isAfter(booking.getEnd()))
                        .collect(Collectors.toList());
            case FUTURE:
                return bookings.stream()
                        .filter(booking -> LocalDateTime.now().isBefore(booking.getStart()))
                        .collect(Collectors.toList());
            case CURRENT:
                return bookings.stream()
                        .filter(booking -> LocalDateTime.now().isAfter(booking.getStart())
                                && LocalDateTime.now().isBefore(booking.getEnd()))
                        .collect(Collectors.toList());
            default:
                throw new ExceptionInteralServerError("Unknown state: " + state);
        }
    }
}
