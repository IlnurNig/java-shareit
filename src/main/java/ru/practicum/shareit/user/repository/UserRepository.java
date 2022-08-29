package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {
    User createUser(User user);

    User updateUser(User user);

    void deleteUser(long userId);

    Optional<User> getUserById(long userId);

    boolean containsId(long userId);

    boolean containsEmail(String email);

    Collection<User> getAllUser();

}
