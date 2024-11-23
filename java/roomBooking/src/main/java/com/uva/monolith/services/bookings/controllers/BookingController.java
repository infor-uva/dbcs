// BookingController.java
package com.uva.monolith.services.bookings.controllers;

import jakarta.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uva.monolith.services.bookings.models.Booking;
import com.uva.monolith.services.bookings.repositories.BookingRepository;
import com.uva.monolith.services.hotels.models.Room;
import com.uva.monolith.services.hotels.repositories.RoomRepository;
import com.uva.monolith.services.users.models.User;
import com.uva.monolith.services.users.repositories.UserRepository;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    public BookingController(BookingRepository bookingRepository, UserRepository userRepository,
            RoomRepository roomRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
    }

    @GetMapping()
    public List<Booking> getAllBookings(
            @RequestParam(required = false) LocalDate start,
            @RequestParam(required = false) LocalDate end,
            @RequestParam(required = false) Integer roomId,
            @RequestParam(required = false) Integer userId) {

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
        if (start == null & end == null && roomId == null && userId == null) {
            bookings = bookingRepository.findAll();
        }
        return bookings;
    }

    @PostMapping
    public Booking createBooking(@RequestBody Booking booking) {
        User user = userRepository.findById(booking.getUserId().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Room room = roomRepository.findById(booking.getRoomId().getId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        // Verificar disponibilidad
        List<Booking> existingBookings = bookingRepository.findByRoomIdAndDateRange(
                room.getId(), booking.getStartDate(), booking.getEndDate());

        if (!existingBookings.isEmpty()) {
            throw new RuntimeException("Room is not available for the selected dates");
        }

        booking.setUserId(user);
        booking.setRoomId(room);
        return bookingRepository.save(booking);
    }

    @GetMapping("/{id}")
    public Booking getBookingById(@PathVariable Integer id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteBooking(@PathVariable Integer id) {
        try {
            if (!bookingRepository.existsById(id))
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);

            bookingRepository.deleteBookingById(id);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
