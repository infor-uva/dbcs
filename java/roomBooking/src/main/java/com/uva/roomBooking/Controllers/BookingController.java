// BookingController.java
package com.uva.roomBooking.Controllers;

import com.uva.roomBooking.Models.Booking;
import com.uva.roomBooking.Models.Room;
import com.uva.roomBooking.Models.User;
import com.uva.roomBooking.Repositories.BookingRepository;
import com.uva.roomBooking.Repositories.RoomRepository;
import com.uva.roomBooking.Repositories.UserRepository;

import jakarta.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@CrossOrigin(origins = "http://localhost:4200")
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

    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
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
