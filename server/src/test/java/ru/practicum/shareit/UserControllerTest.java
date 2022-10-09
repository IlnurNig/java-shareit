package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.abstractClass.ExceptionBadRequest;
import ru.practicum.shareit.exception.abstractClass.ExceptionConflict;
import ru.practicum.shareit.exception.abstractClass.ExceptionNotFound;
import ru.practicum.shareit.exception.iml.UnknownUserException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    private static final String URL = "/users";

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private List<UserDto> userUploaded;
    private UserDto userDto;


    @BeforeEach
    void init() throws ExceptionBadRequest, ExceptionConflict, ExceptionNotFound {
        userUploaded = new ArrayList<>();
        for (int i = 1; i < 11; i++) {
            userDto = UserDto.builder().name("user" + i).email("user" + i + "@mail").build();
            userUploaded.add(userDto);
            userService.createUser(userDto);
        }
    }

    @AfterEach
    void terminate() {
        userRepository.deleteAll();
    }


    @Test
    void getUserDtoById() throws Exception {
        List<UserDto> userUnloaded = (List<UserDto>) userService.getAllUserDto();
        mvc.perform(get(URL + "/{id}", userUnloaded.get(5).getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(userUploaded.get(5).getName())));

    }


    @Test
    void createUser() throws Exception {
        userDto = UserDto.builder().name("userTest").email("usertest@mail").build();
        mvc.perform(post(URL)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andReturn();
    }

    @Test
    void updateUser() throws Exception {
        userDto = userService.getAllUserDto().stream().findFirst().orElseThrow();
        userDto.setName("newName");
        userDto.setEmail("new@new");

        mvc.perform(patch(URL + "/{id}", userDto.getId())
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());
        getUser(userDto);
    }

    void getUser(UserDto userDto) throws Exception {
        mvc.perform(get(URL + "/{id}", userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(userDto.getName())));
    }

    @Test
    void deleteUser() throws Exception {
        userDto = userService.getAllUserDto().stream().findFirst().orElseThrow();
        long id = userDto.getId();
        mvc.perform(delete(URL + "/{id}", id))
                .andExpect(status().isOk());
        assertFalse(userService.containsId(id));
        assertThrows(UnknownUserException.class, () -> userService.getUserById(id));

    }

    @Test
    void getAllUser() throws Exception {
        userDto = userService.getAllUserDto().stream().findFirst().orElseThrow();
        User user = userService.getUserById(userDto.getId() + 4);
        mvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(10)))
                .andExpect(jsonPath("$[4].name", is(user.getName())));
    }

}