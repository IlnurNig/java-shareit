package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserRepositoryInMemory implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private static long countUser;

    @Override
    public User createUser(User user) {
        user.setId(++countUser);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteUser(long userId) {
        users.remove(userId);
    }

    @Override
    public Optional<User> getUserById(long userId) {
        if (users.containsKey(userId))
            return Optional.of(users.get(userId));
        return Optional.empty();
    }

    @Override
    public boolean containsId(long userId) {
        return users.containsKey(userId);
    }

    @Override
    public boolean containsEmail(String email) {
        return users.values().stream()
                .map(User::getEmail)
                .anyMatch(a -> a.equals(email));
    }

    @Override
    public Collection<User> getAllUser() {
        return users.values();
    }
}
