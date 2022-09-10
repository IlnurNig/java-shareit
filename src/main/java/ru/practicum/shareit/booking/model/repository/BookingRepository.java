package ru.practicum.shareit.booking.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findBookingByBookerIdOrderByStartDesc(long bookerId);

    List<Booking> findBookingByItem_User_IdOrderByStartDesc(long userId);

}
