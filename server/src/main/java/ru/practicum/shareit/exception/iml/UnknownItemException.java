package ru.practicum.shareit.exception.iml;

import ru.practicum.shareit.exception.abstractClass.ExceptionNotFound;

public class UnknownItemException extends ExceptionNotFound {
    public UnknownItemException(String message) {
        super(message);
    }
}
