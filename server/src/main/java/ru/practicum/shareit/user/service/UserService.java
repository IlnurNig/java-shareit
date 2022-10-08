package ru.practicum.shareit.user.service;

import ru.practicum.shareit.exception.abstractClass.ExceptionBadRequest;
import ru.practicum.shareit.exception.abstractClass.ExceptionConflict;
import ru.practicum.shareit.exception.abstractClass.ExceptionNotFound;
import ru.practicum.shareit.exception.iml.ConflictUserException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {
    UserDto createUser(UserDto userDto) throws ExceptionNotFound, ExceptionConflict, ExceptionBadRequest;

    UserDto updateUser(UserDto userDto) throws ExceptionBadRequest, ExceptionNotFound, ConflictUserException;

    void deleteUser(long userId);

    UserDto getUserDtoById(long userId) throws ExceptionNotFound;

    Collection<UserDto> getAllUserDto();

    boolean containsId(long userId);

    User getUserById(long userId) throws ExceptionNotFound;

}
