package ru.practicum.shareit.user.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

@Component
public class UserMapper {
    public static User toEntity(UserDto dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setId(dto.getId());
        user.setName(dto.getName());
        return user;
    }

    public static UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
