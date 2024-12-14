package com.uva.api.services.bookings.services;

import com.uva.api.services.bookings.models.Booking;
import com.uva.api.services.bookings.repositories.BookingRepository;
import com.uva.api.services.hotels.models.Room;
import com.uva.api.services.hotels.repositories.RoomRepository;
import com.uva.api.services.users.models.Client;
import com.uva.api.services.users.repositories.ClientRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ClientRepository clientRepository;

    public List<Booking> getBookings(LocalDate start, LocalDate end, Integer roomId, Integer userId) {
        List<Booking> bookings = null;

        if (start != null && end != null) {
            bookings = bookingRepository.findByDateRange(start, end);
        }
        if (roomId != null) {
            if (bookings == null) {
                bookings = bookingRepository.findByRoomId(roomId);
            } else {
                bookings = bookings.stream()
                        .filter(booking -> booking.getRoomId().getId() == roomId)
                        .toList();
            }
        }
        if (userId != null) {
            if (bookings == null) {
                bookings = bookingRepository.findByUserId(userId);
            } else {
                bookings = bookings.stream()
                        .filter(booking -> booking.getUserId().getId() == userId)
                        .toList();
            }
        }
        if (start == null && end == null && roomId == null && userId == null) {
            bookings = bookingRepository.findAll();
        }

        return bookings;
    }

    public Booking createBooking(Booking booking) {
        Client user = clientRepository.findById(booking.getUserId().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Room room = roomRepository.findById(booking.getRoomId().getId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        // Check availability
        List<Booking> existingBookings = bookingRepository.findByRoomIdAndDateRange(
                room.getId(), booking.getStartDate(), booking.getEndDate());

        if (!existingBookings.isEmpty()) {
            throw new RuntimeException("Room is not available for the selected dates");
        }

        booking.setUserId(user);
        booking.setRoomId(room);
        return bookingRepository.save(booking);
    }

    public Booking getBookingById(Integer id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    public void deleteBooking(Integer id) {
        if (!bookingRepository.existsById(id)) {
            throw new RuntimeException("Booking not found");
        }
        bookingRepository.deleteBookingById(id);
    }
}
