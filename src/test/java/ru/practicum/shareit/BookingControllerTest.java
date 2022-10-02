package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.booking.model.status.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.abstractClass.ExceptionBadRequest;
import ru.practicum.shareit.exception.abstractClass.ExceptionConflict;
import ru.practicum.shareit.exception.abstractClass.ExceptionNotFound;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BookingControllerTest {
    private static final String URL = "/bookings";

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentRepository commentRepository;

    private List<ItemDto> itemsUploaded;

    private List<UserDto> userUploaded;

    private BookingOutputDto bookingOutputDto;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void init() throws ExceptionBadRequest, ExceptionConflict, ExceptionNotFound {
        userUploaded = new ArrayList<>();
        for (int i = 1; i < 11; i++) {
            UserDto userDto = UserDto.builder().name("user" + i).email("user" + i + "@mail").build();
            userUploaded.add(userService.createUser(userDto));
        }

        itemsUploaded = new ArrayList<>();
        ItemDto itemDto;
        for (int i = 1; i < 4; i++) {
            itemDto = ItemDto.builder().name("item" + i).description("desc" + i).available(true).build();
            itemsUploaded.add(itemService.createItem(itemDto, userUploaded.get(1).getId()));
        }
        for (int i = 5; i < 8; i++) {
            itemDto = ItemDto.builder().name("item" + i).description("desc" + i).available(true).build();
            itemsUploaded.add(itemService.createItem(itemDto, userUploaded.get(2).getId()));
        }

        BookingDto bookingDto = BookingDto.builder()
                .itemId(itemsUploaded.get(0).getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(5))
                .status(Status.APPROVED.getCode())
                .bookerId(userUploaded.get(5).getId())
                .build();
        bookingOutputDto = bookingService.createBookingDto(bookingDto, bookingDto.getBookerId());

    }

    @AfterEach
    void terminate() {
        commentRepository.deleteAll();
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void createBookingDto() throws Exception {
        BookingDto bookingDto = BookingDto.builder()
                .itemId(itemsUploaded.get(1).getId())
                .start(LocalDateTime.now().plusDays(10))
                .end(LocalDateTime.now().plusDays(50))
                .status(Status.APPROVED.getCode())
                .bookerId(userUploaded.get(7).getId())
                .build();

        mvc.perform(post(URL + "/")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", userUploaded.get(7).getId()))
                .andExpect(status().isOk());

        assertEquals(2, bookingRepository.findAll().size());
    }

    @Test
    void approve() throws Exception {

        mvc.perform(MockMvcRequestBuilders.patch(URL + "/{bookingId}", bookingOutputDto.getId())
                        .header("X-Sharer-User-Id", userUploaded.get(1).getId())
                        .param("approved", String.valueOf(true)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("APPROVED")));

    }

    @Test
    void getBookingByBookingIdAndUserId() throws Exception {
        mvc.perform(get(URL + "/{bookingId}", bookingOutputDto.getId())
                        .header("X-Sharer-User-Id", userUploaded.get(1).getId()))
                .andExpect(status().isOk());
    }

    @Test
    void getAllBookingByUserIdAndState() throws Exception {
        mvc.perform(get(URL + "/")
                        .header("X-Sharer-User-Id", userUploaded.get(5).getId())
                        .param("state", String.valueOf(State.ALL)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    void getAllBookingByOwnerIdAndState() throws Exception {
        mvc.perform(get(URL + "/owner")
                        .header("X-Sharer-User-Id", userUploaded.get(1).getId())
                        .param("state", String.valueOf(State.ALL)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
    }


    @Test
    void approve_incorrectBookingId() throws Exception {

        mvc.perform(MockMvcRequestBuilders.patch(URL + "/{bookingId}", 100)
                        .header("X-Sharer-User-Id", userUploaded.get(1).getId())
                        .param("approved", String.valueOf(true)))
                .andExpect(status().isNotFound());

    }

    @Test
    void approve_incorrectUserId() throws Exception {

        mvc.perform(MockMvcRequestBuilders.patch(URL + "/{bookingId}", bookingOutputDto.getId())
                        .header("X-Sharer-User-Id", 100)
                        .param("approved", String.valueOf(true)))
                .andExpect(status().isNotFound());

    }

    @Test
    void approve_previouslyApproved() throws Exception {
        mvc.perform(MockMvcRequestBuilders.patch(URL + "/{bookingId}", bookingOutputDto.getId())
                        .header("X-Sharer-User-Id", userUploaded.get(1).getId())
                        .param("approved", String.valueOf(true)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("APPROVED")));

        mvc.perform(MockMvcRequestBuilders.patch(URL + "/{bookingId}", bookingOutputDto.getId())
                        .header("X-Sharer-User-Id", userUploaded.get(1).getId())
                        .param("approved", String.valueOf(true)))
                .andExpect(status().isBadRequest());

    }

    @Test
    void approve_rejected() throws Exception {

        mvc.perform(MockMvcRequestBuilders.patch(URL + "/{bookingId}", bookingOutputDto.getId())
                        .header("X-Sharer-User-Id", userUploaded.get(1).getId())
                        .param("approved", String.valueOf(false)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("REJECTED")));

    }


    @Test
    void getBookingByBookingIdAndUserId_bookingIdEmpty() throws Exception {
        mvc.perform(get(URL + "/{bookingId}", 100)
                        .header("X-Sharer-User-Id", userUploaded.get(1).getId()))
                .andExpect(status().isNotFound());
    }


    @Test
    void getBookingByBookingIdAndUserId_bookerIncorrect() throws Exception {
        mvc.perform(get(URL + "/{bookingId}", bookingOutputDto.getId())
                        .header("X-Sharer-User-Id", userUploaded.get(7).getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllBookingByUserIdAndState_wrongUser() throws Exception {
        mvc.perform(get(URL + "/")
                        .header("X-Sharer-User-Id", 100)
                        .param("state", String.valueOf(State.ALL)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getAllBookingByOwnerIdAndState_wrongUser() throws Exception {
        mvc.perform(get(URL + "/owner")
                        .header("X-Sharer-User-Id", 100)
                        .param("state", String.valueOf(State.ALL)))
                .andExpect(status().isInternalServerError());
    }

}
