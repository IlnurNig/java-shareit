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
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.status.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.abstractClass.ExceptionBadRequest;
import ru.practicum.shareit.exception.abstractClass.ExceptionConflict;
import ru.practicum.shareit.exception.abstractClass.ExceptionNotFound;
import ru.practicum.shareit.exception.iml.UnknownItemException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ItemControllerTest {

    private static final String URL = "/items";
    private static final String SEARCH_URL = "/search";

    private static final String COMMENT_URL = "/comment";

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

    private ItemDto itemDto;

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
        bookingService.approve(userUploaded.get(1).getId(), bookingOutputDto.getId(), true);

    }

    @AfterEach
    void terminate() {
        commentRepository.deleteAll();
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void createItem() throws Exception {
        itemDto = ItemDto.builder().name("itemNew").description("descNew").available(true).build();

        var result = mvc.perform(post(URL + "/")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", userUploaded.get(3).getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andReturn();

        var itemFromJson = mapper.readValue(result.getResponse().getContentAsString(), ItemDto.class);
        ItemDto itemDb = itemService.getItemDtoById(itemFromJson.getId(), userUploaded.get(3).getId());

        if (itemFromJson.getComments() == null) itemFromJson.setComments(new HashSet<>());
        assertNotNull(itemDb);
        assertEquals(itemDb, itemFromJson);

    }

    @Test
    void getItemById() throws Exception {
        mvc.perform(get(URL + "/{id}", itemsUploaded.get(1).getId())
                        .header("X-Sharer-User-Id", userUploaded.get(1).getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemsUploaded.get(1).getName())));

    }

    @Test
    void updateItem() throws Exception {
        itemDto = itemsUploaded.get(1);
        itemDto.setName("newName");

        mvc.perform(patch(URL + "/{id}", itemDto.getId())
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", userUploaded.get(1).getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemDto.getName())));
    }

    @Test
    void getAllItemDtoByIdUser() throws Exception {
        mvc.perform(get(URL + "/").header("X-Sharer-User-Id", userUploaded.get(1).getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(3)))
                .andExpect(jsonPath("$[0].name", is(itemsUploaded.get(0).getName())));
    }


    @Test
    void searchItemDtoByIdUserAndByText() throws Exception {
        User user = userService.getUserById(userUploaded.get(1).getId());

        mvc.perform(get(URL + SEARCH_URL)
                        .param("text", "item")
                        .header("X-Sharer-User-Id", userUploaded.get(1).getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(user.getItems().size() + 3)));
    }

    @Test
    void deleteItem() throws ExceptionNotFound {
        itemDto = itemsUploaded.get(1);
        itemDto = itemService.getItemDtoById(itemsUploaded.get(1).getId(), userUploaded.get(2).getId());

        itemService.deleteItem(itemDto.getId());
        assertThrows(UnknownItemException.class, () -> itemService.getItemById(itemDto.getId()));
        assertThrows(UnknownItemException.class, () -> itemService.getItemDtoById(itemDto.getId(), 2));
    }

    @Test
    void searchItemDtoByIdUserAndByText_emptyText() throws Exception {
        mvc.perform(get(URL + SEARCH_URL)
                        .param("text", " ")
                        .header("X-Sharer-User-Id", userUploaded.get(1).getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(0)));
    }


    @Test
    void createItem_userIdNotExist() throws Exception {
        itemDto = ItemDto.builder().name("itemNew").description("descNew").available(true).build();

        mvc.perform(post(URL + "/")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 100))
                .andExpect(status().isNotFound());
    }

    @Test
    void createItem_availableEmpty() throws Exception {
        itemDto = ItemDto.builder().name("itemNew").description("descNew").available(null).build();

        mvc.perform(post(URL + "/")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", userUploaded.get(3).getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createItem_nameEmpty() throws Exception {
        itemDto = ItemDto.builder().name(" ").description("descNew").available(true).build();

        mvc.perform(post(URL + "/")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", userUploaded.get(3).getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createItem_descriptionEmpty() throws Exception {
        itemDto = ItemDto.builder().name("testt").description(" ").available(true).build();

        mvc.perform(post(URL + "/")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", userUploaded.get(3).getId()))
                .andExpect(status().isBadRequest());
    }


    @Test
    void updateItem_incorrectUserId() throws Exception {
        itemDto = itemsUploaded.get(1);
        itemDto.setName("newName");

        mvc.perform(patch(URL + "/{id}", itemDto.getId())
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", userUploaded.get(5).getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void createComment() throws Exception {

        CommentDto commentDto = CommentDto.builder()
                .text("Test comment")
                .build();

        Booking booking = bookingRepository.findById(bookingOutputDto.getId()).orElseThrow();
        booking.setStart(LocalDateTime.now().minusDays(1));
        bookingRepository.save(booking);

        mvc.perform(post(URL + "/{itemId}" + COMMENT_URL, itemsUploaded.get(0).getId())
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", userUploaded.get(5).getId())
                        .content(mapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(commentDto.getText())));

        Comment comment = commentRepository.findById(1L).orElseThrow();
        assertNotNull(comment);
        assertNotNull(comment.getItem());
        assertNotNull(comment.getAuthor());
    }


}
