package ru.practicum.shareit.exception.abstractClass;

public abstract class ExceptionBadRequest extends Exception {
    public ExceptionBadRequest(String message) {
        super(message);
    }
}
