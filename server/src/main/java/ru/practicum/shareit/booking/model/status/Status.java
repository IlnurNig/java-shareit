package ru.practicum.shareit.booking.model.status;

public enum Status {
    WAITING("WAITING"), APPROVED("APPROVED"), REJECTED("REJECTED"), CANCELED("CANCELED");

    private String code;

    private Status(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
