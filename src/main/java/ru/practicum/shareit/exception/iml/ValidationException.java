package ru.practicum.shareit.exception.iml;

import ru.practicum.shareit.exception.abstractClass.ExceptionBadRequest;

public class ValidationException extends ExceptionBadRequest {
    public ValidationException(String message) {
        super(message);
    }
}
