package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
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
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.repository.RequestRepository;
import ru.practicum.shareit.requests.service.RequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ItemRequestControllerTest {

    private static final String URL = "/requests";

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

    @Autowired
    private RequestService requestService;

    @Autowired
    private RequestRepository requestRepository;

    private List<UserDto> userUploaded;

    private List<ItemRequestDto> itemRequestDtoList;

    ItemRequestDto itemRequestDto;

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

        List<ItemDto> itemsUploaded = new ArrayList<>();
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
        BookingOutputDto bookingOutputDto = bookingService.createBookingDto(bookingDto, bookingDto.getBookerId());
        bookingService.approve(userUploaded.get(1).getId(), bookingOutputDto.getId(), true);

        itemRequestDtoList = new ArrayList<>();
        for (int i = 1; i < 11; i++) {
            itemRequestDto = ItemRequestDto.builder()
                    .created(LocalDateTime.now())
                    .description("test request" + i)
                    .build();
            itemRequestDtoList.add(requestService.createRequest(userUploaded.get(1).getId(), itemRequestDto));
        }


    }

    @AfterEach
    void terminate() {
        requestRepository.deleteAll();
        commentRepository.deleteAll();
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void createRequest() throws Exception {
        itemRequestDto = ItemRequestDto.builder()
                .created(LocalDateTime.now())
                .description("test request new")
                .build();
        mvc.perform(post(URL + "/")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .header("X-Sharer-User-Id", userUploaded.get(5).getId()))
                .andExpect(status().isOk());
    }

    @Test
    void getRequestDtoById() throws Exception {
        var result = mvc.perform(get(URL + "/{requestId}", itemRequestDtoList.get(1).getId())
                        .header("X-Sharer-User-Id", userUploaded.get(1).getId()))
                .andExpect(status().isOk())
                .andReturn();

        var itemRequestFromJson = mapper.readValue(result.getResponse().getContentAsString(),
                ItemRequestDto.class);

        assertNotNull(itemRequestFromJson);
        assertEquals(itemRequestFromJson.getDescription(), itemRequestDtoList.get(1).getDescription());
    }


    @Test
    void getAllRequestByUserId() throws Exception {
        mvc.perform(get(URL + "/").header("X-Sharer-User-Id", userUploaded.get(1).getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(10)));
    }

    @Test
    void getAllRequest() throws Exception {
        mvc.perform(get(URL + "/all").header("X-Sharer-User-Id", userUploaded.get(7).getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(10)));
    }

}
