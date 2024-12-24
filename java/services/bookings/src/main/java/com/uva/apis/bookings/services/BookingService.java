package com.uva.apis.bookings.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.uva.apis.bookings.api.HotelApi;
import com.uva.apis.bookings.api.ClientApi;
import com.uva.apis.bookings.models.Booking;
import com.uva.apis.bookings.repositories.BookingRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private HotelApi hotelApi;

    @Autowired
    private ClientApi managerApi;

    /**
     * Consulta por bloques filtrados
     * - fechas
     * - roomId/hotelId
     * - userId
     * 
     * @param start
     * @param end
     * @param hotelId
     * @param roomId
     * @param userId
     * @return
     */
    public List<Booking> getBookings(
            LocalDate start, LocalDate end, Integer hotelId,
            Integer roomId, Integer userId) {
        List<Booking> bookings = null;

        if (start != null && end != null) {
            bookings = bookingRepository.findByDateRange(start, end);
        }

        if (roomId != null) {
            if (bookings == null) {
                bookings = bookingRepository.findByRoomId(roomId);
            } else {
                bookings = bookings.stream()
                        .filter(booking -> booking.getRoomId() == roomId)
                        .toList();
            }
        } else if (hotelId != null) {
            if (bookings == null) {
                bookings = bookingRepository.findByHotelId(roomId);
            } else {
                bookings = bookings.stream()
                        .filter(booking -> booking.getHotelId() == hotelId)
                        .toList();
            }
        }

        if (userId != null) {
            if (bookings == null) {
                bookings = bookingRepository.findByUserId(userId);
            } else {
                bookings = bookings.stream()
                        .filter(booking -> booking.getUserId() == userId)
                        .toList();
            }
        }

        if (start == null && end == null && roomId == null && userId == null) {
            bookings = bookingRepository.findAll();
        }

        return bookings;
    }

    public Booking createBooking(Booking booking) {
        int userId = booking.getUserId();
        int roomId = booking.getRoomId();
        int hotelId = booking.getHotelId();

        // Check if the customer and rooms exists
        if (!managerApi.existsById(userId))
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "User not found");

        if (!hotelApi.existsById(hotelId, roomId))
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "room not found");

        // Check availability
        List<Booking> existingBookings = bookingRepository.findByRoomIdAndDateRange(roomId, booking.getStartDate(),
                booking.getEndDate());

        if (!existingBookings.isEmpty()) {
            throw new RuntimeException("Room is not available for the selected dates");
        }

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
        bookingRepository.deleteById(id);
    }

    public List<Booking> deleteBookingsByHotelId(int hotelId) {
        // Extraer reservas realizadas al hotel
        List<Booking> bookings = bookingRepository.findByHotelId(hotelId);
        if (bookings.isEmpty()) {
            return new ArrayList<>();
        }
        bookingRepository.deleteAllByHotelId(hotelId);
        return bookings;
    }

    public List<Booking> deleteAllByManagerId(int managerId) {
        List<Booking> bookings = bookingRepository.findByManagerId(managerId);
        if (bookings.isEmpty()) {
            return new ArrayList<>();
        }
        bookingRepository.deleteAllByManagerId(managerId);
        return bookings;
    }
}
