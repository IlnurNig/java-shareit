package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;

/**
 * // TODO .
 */
@Data
@Builder
public class User {
    private long id;
    private String name;
    private String email;
}
