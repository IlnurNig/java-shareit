package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.abstractClass.ExceptionBadRequest;
import ru.practicum.shareit.exception.abstractClass.ExceptionNotFound;
import ru.practicum.shareit.exception.iml.ExceptionInteralServerError;
import ru.practicum.shareit.exception.iml.ValidationException;

import javax.validation.constraints.NotNull;
import java.util.Collection;

/**
 * // TODO .
 */
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingOutputDto createBookingDto(@RequestBody BookingDto bookingDto,
                                             @RequestHeader("X-Sharer-User-Id") long bookerId) throws ExceptionNotFound,
            ValidationException {

        return bookingService.createBookingDto(bookingDto, bookerId);
    }

    @PatchMapping("{bookingId}")
    public BookingOutputDto approve(
            @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
            @PathVariable @NotNull Long bookingId,
            @RequestParam @NotNull Boolean approved) throws ExceptionNotFound, ExceptionBadRequest {
        return bookingService.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingOutputDto getBookingByBookingIdAndUserId(@PathVariable long bookingId,
                                                           @RequestHeader("X-Sharer-User-Id") @NotNull Long userId)
            throws ExceptionNotFound {
        return bookingService.getBookingByBookingIdAndUserId(bookingId, userId);
    }

    @GetMapping
    public Collection<BookingOutputDto> getAllBookingByUserIdAndState(
            @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(name = "from", required = false) Integer from,
            @RequestParam(name = "size", required = false) Integer size)
            throws ExceptionInteralServerError, ExceptionBadRequest {

        return bookingService.getAllBookingByUserIdAndState(userId, state, from, size);
    }

    @GetMapping("owner")
    public Collection<BookingOutputDto> getAllBookingByOwnerIdAndState(
            @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(name = "from", required = false) Integer from,
            @RequestParam(name = "size", required = false) Integer size)
            throws ExceptionInteralServerError, ValidationException {

        return bookingService.getAllBookingByOwnerIdAndState(userId, state, from, size);
    }

}
