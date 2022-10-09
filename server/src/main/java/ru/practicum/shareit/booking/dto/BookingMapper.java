package ru.practicum.shareit.booking.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.dto.UserMapper;


@Component
public class BookingMapper {
    public static Booking toEntity(BookingDto dto) {
        Booking booking = new Booking();
        booking.setBookingId(dto.getId());
        booking.setEnd(dto.getEnd());
        booking.setStart(dto.getStart());
        return booking;
    }

    public static BookingOutputDto toDto(Booking booking) {
        return BookingOutputDto.builder()
                .booker(UserMapper.toDto(booking.getBooker()))
                .status(booking.getStatus().getCode())
                .id(booking.getBookingId())
                .startTime(booking.getStart())
                .endTime(booking.getEnd())
                .item(ItemMapper.toDto(booking.getItem()))
                .bookerId(booking.getBooker().getId())
                .build();
    }
}
