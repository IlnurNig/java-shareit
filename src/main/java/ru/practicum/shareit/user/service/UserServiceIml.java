package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exception.iml.ConflictUserException;
import ru.practicum.shareit.exception.iml.UnknownUserException;
import ru.practicum.shareit.exception.iml.ValidationException;
import ru.practicum.shareit.exception.abstractClass.ExceptionBadRequest;
import ru.practicum.shareit.exception.abstractClass.ExceptionConflict;
import ru.practicum.shareit.exception.abstractClass.ExceptionNotFound;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceIml implements UserService {
    private final UserRepository repository;
    private final UserMapper mapper;

    @Autowired
    public UserServiceIml(UserRepository repository, UserMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public UserDto createUser(UserDto userDto) throws ExceptionNotFound, ExceptionConflict, ExceptionBadRequest {
        validateCreateUser(userDto);
        validateDuplicateEmail(userDto.getEmail());
        User user = repository.createUser(mapper.toEntity(userDto));
        return mapper.toDto(user);
    }

    @Override
    public UserDto updateUser(UserDto userDto) throws ExceptionBadRequest, ExceptionNotFound, ConflictUserException {
        validateUpdateUser(userDto);
        User user = getUserById(userDto.getId());
        if (!(userDto.getEmail() == null)) {
            validateDuplicateEmail(userDto.getEmail());
            user.setEmail(userDto.getEmail());
        }
        if (!(userDto.getName() == null)) {
            user.setName(userDto.getName());
        }
        return mapper.toDto(repository.updateUser(user));
    }

    @Override
    public void deleteUser(long userId) {
        repository.deleteUser(userId);
    }

    @Override
    public UserDto getUserDtoById(long userId) throws ExceptionNotFound {
        return mapper.toDto(getUserById(userId));
    }

    @Override
    public Collection<UserDto> getAllUserDto() {
        return repository.getAllUser().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public boolean containsId(long userId) {
        return repository.containsId(userId);
    }

    private User getUserById(long userId) throws ExceptionNotFound {
        return repository.getUserById(userId).orElseThrow(() ->
                new UnknownUserException(String.format("The user with id=%d does not exist", userId)));
    }

    private void validateCreateUser(UserDto userDto) throws UnknownUserException, ExceptionBadRequest {
        if (!StringUtils.hasText(userDto.getName())) {
            throw new UnknownUserException("The name cannot be empty");
        }
        if (!StringUtils.hasText(userDto.getEmail()))
            throw new ValidationException("Email not be empty");
        validateUpdateUser(userDto);
    }

    private void validateUpdateUser(UserDto userDto) throws UnknownUserException, ValidationException {
        if (StringUtils.containsWhitespace(userDto.getName()))
            throw new UnknownUserException("The name cannot contain a space");
        if (!(userDto.getEmail() == null) && !userDto.getEmail().contains("@"))
            throw new ValidationException("Email must contain @");
    }

    private void validateDuplicateEmail(String email) throws ConflictUserException {
        if (repository.containsEmail(email))
            throw new ConflictUserException(String.format("User with mail=%s already exists", email));
    }

}
