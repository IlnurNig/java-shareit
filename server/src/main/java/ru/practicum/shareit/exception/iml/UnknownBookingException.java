package ru.practicum.shareit.exception.iml;

import ru.practicum.shareit.exception.abstractClass.ExceptionNotFound;

public class UnknownBookingException extends ExceptionNotFound {
    public UnknownBookingException(String message) {
        super(message);
    }
}
