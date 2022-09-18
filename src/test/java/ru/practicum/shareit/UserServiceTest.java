package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.abstractClass.ExceptionBadRequest;
import ru.practicum.shareit.exception.abstractClass.ExceptionConflict;
import ru.practicum.shareit.exception.abstractClass.ExceptionNotFound;
import ru.practicum.shareit.exception.iml.ConflictUserException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {

    @Autowired
    UserService userService;

    @Test
    void userServiceTest() throws ExceptionBadRequest, ExceptionConflict, ExceptionNotFound {

        UserDto userDto = UserDto.builder()
                .name("testName")
                .email("test@test")
                .build();
        UserDto userCheck = userService.createUser(userDto);
        assertEquals(userDto.getName(), userCheck.getName());
        assertEquals(userDto.getEmail(), userCheck.getEmail());
        assertEquals(1, userCheck.getId());

        userDto = UserDto.builder()
                .name("user")
                .email("user@user")
                .build();
        userCheck = userService.createUser(userDto);
        assertEquals(userDto.getName(), userCheck.getName());
        assertEquals(userDto.getEmail(), userCheck.getEmail());
        assertEquals(2, userCheck.getId());

        UserDto finalUserDto = userDto;
        assertThrows(DataIntegrityViolationException.class, () -> userService.createUser(finalUserDto));

        assertThrows(ExceptionNotFound.class, () -> userService.getUserDtoById(100));
        assertThrows(ExceptionNotFound.class, () -> userService.getUserDtoById(-1));

        userDto = UserDto.builder()
                .id(1)
                .name("testName")
                .email("test@test")
                .build();
        assertEquals(userDto, userService.getUserDtoById(1));

        userDto = userService.getUserDtoById(1);
        userDto.setName("updateName");
        UserDto finalUserDto1 = userDto;
        assertThrows(ConflictUserException.class, () -> userService.updateUser(finalUserDto1));
        userDto.setEmail("user@user");
        UserDto finalUserDto2 = userDto;
        assertThrows(ConflictUserException.class, () -> userService.updateUser(finalUserDto2));
        userDto.setEmail("update@user");
        userService.updateUser(userDto);
        assertEquals(userDto, userService.getUserDtoById(1));
    }


}
