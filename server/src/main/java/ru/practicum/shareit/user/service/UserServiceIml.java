package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.abstractClass.ExceptionNotFound;
import ru.practicum.shareit.exception.iml.ConflictUserException;
import ru.practicum.shareit.exception.iml.UnknownUserException;
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

    @Autowired
    public UserServiceIml(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDto createUser(UserDto userDto) throws ConflictUserException {
        try {
            User user = repository.save(UserMapper.toEntity(userDto));
            return UserMapper.toDto(user);
        } catch (Exception e) {
            throw new ConflictUserException(String.format("User with mail=%s already exists", userDto.getEmail()));
        }
    }

    @Override
    public UserDto updateUser(UserDto userDto) throws ExceptionNotFound {
        User user = getUserById(userDto.getId());
        if (!(userDto.getEmail() == null)) {
            user.setEmail(userDto.getEmail());
        }
        if (!(userDto.getName() == null)) {
            user.setName(userDto.getName());
        }
        return UserMapper.toDto(repository.save(user));
    }

    @Override
    public void deleteUser(long userId) {
        repository.deleteById(userId);
    }

    @Override
    public UserDto getUserDtoById(long userId) throws ExceptionNotFound {
        return UserMapper.toDto(getUserById(userId));
    }

    @Override
    public Collection<UserDto> getAllUserDto() {
        return repository.findAll().stream().map(UserMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public boolean containsId(long userId) {
        return repository.findById(userId).isPresent();
    }

    public User getUserById(long userId) throws ExceptionNotFound {
        return repository.findById(userId).orElseThrow(() ->
                new UnknownUserException(String.format("The user with id=%d does not exist", userId)));
    }

}
