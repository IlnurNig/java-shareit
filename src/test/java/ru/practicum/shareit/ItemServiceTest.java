package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.abstractClass.ExceptionBadRequest;
import ru.practicum.shareit.exception.abstractClass.ExceptionConflict;
import ru.practicum.shareit.exception.abstractClass.ExceptionNotFound;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {

    @Autowired
    ItemService itemService;

    @Autowired
    private final UserService userService;


    @Test
    void itemServiceTest() throws ExceptionBadRequest, ExceptionNotFound, ExceptionConflict {

        UserDto userDto = userService.createUser(UserDto.builder().name("userService1").email("userService1@mail").build());
        UserDto userDto2 = userService.createUser(UserDto.builder().name("userService2").email("userService2@mail").build());

        ItemDto itemDto = ItemDto.builder()
                .name("item1")
                .description("desc1")
                .available(true)
                .build();


        ItemDto itemDtoNew = itemService.createItem(itemDto, userDto.getId());
        assertEquals(itemDto.getAvailable(), itemDtoNew.getAvailable());
        assertEquals(itemDto.getName(), itemDtoNew.getName());
        assertEquals(itemDto.getDescription(), itemDtoNew.getDescription());

        itemDto = itemService.getItemDtoById(1, userDto2.getId());
        assertEquals(itemDto.getAvailable(), itemDtoNew.getAvailable());
        assertEquals(itemDto.getName(), itemDtoNew.getName());
        assertEquals(itemDto.getDescription(), itemDtoNew.getDescription());

    }
}
