package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.model.status.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.abstractClass.ExceptionBadRequest;
import ru.practicum.shareit.exception.abstractClass.ExceptionConflict;
import ru.practicum.shareit.exception.abstractClass.ExceptionNotFound;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.service.RequestService;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestServiceTest {
    @Autowired
    RequestService requestService;
    @Autowired
    BookingService bookingService;
    @Autowired
    UserService userService;
    @Autowired
    ItemService itemService;

    @Test
    void requestServiceTest() throws ExceptionBadRequest, ExceptionNotFound {
        itemService.createItem(ItemDto.builder().name("item1").description("desc1").available(true).build(), 1);

        BookingDto bookingDto = BookingDto.builder()
                .bookerId(2L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(Status.WAITING.getCode())
                .itemId(1L)
                .build();
        BookingOutputDto bookingDtoNew = bookingService.createBookingDto(bookingDto, 2);

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("test")
                .created(LocalDateTime.now())
                .build();

        ItemRequestDto itemRequestDtoNew = requestService.createRequest(1L, itemRequestDto);
        assertEquals(itemRequestDto.getDescription(), itemRequestDtoNew.getDescription());
        assertEquals(itemRequestDto.getCreated(), itemRequestDtoNew.getCreated());

    }

}
