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

        userService.createUser(UserDto.builder().name("test").email("test@maeil").build());

        ItemDto itemDto = ItemDto.builder()
                .name("item1")
                .description("desc1")
                .available(true)
                .build();


        ItemDto itemDtoNew = itemService.createItem(itemDto, 1);
        assertEquals(itemDto.getAvailable(), itemDtoNew.getAvailable());
        assertEquals(itemDto.getName(), itemDtoNew.getName());
        assertEquals(itemDto.getDescription(), itemDtoNew.getDescription());

        itemDto = itemService.getItemDtoById(1, 2);
        assertEquals(itemDto.getAvailable(), itemDtoNew.getAvailable());
        assertEquals(itemDto.getName(), itemDtoNew.getName());
        assertEquals(itemDto.getDescription(), itemDtoNew.getDescription());

    }
}
