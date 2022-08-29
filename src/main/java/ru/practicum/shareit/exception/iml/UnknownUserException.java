package ru.practicum.shareit.exception.iml;

import ru.practicum.shareit.exception.abstractClass.ExceptionNotFound;

public class UnknownUserException extends ExceptionNotFound {
    public UnknownUserException(String message) {
        super(message);
    }
}
