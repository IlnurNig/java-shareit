package ru.practicum.shareit.exception.iml;

import ru.practicum.shareit.exception.abstractClass.ExceptionConflict;

public class ConflictUserException extends ExceptionConflict {
    public ConflictUserException(String message) {
        super(message);
    }
}
