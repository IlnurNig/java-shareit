package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UserDto {
    private long id;
    @NotBlank
    private String name;
    @Email
    @NotBlank
    private String email;
}
