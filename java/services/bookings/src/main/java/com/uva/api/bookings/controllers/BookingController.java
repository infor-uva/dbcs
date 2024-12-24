package com.uva.api.bookings.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uva.api.bookings.models.Booking;
import com.uva.api.bookings.services.BookingService;

import java.time.LocalDate;

@RestController
@RequestMapping("/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @GetMapping
    public ResponseEntity<?> getAllBookings(
            @RequestParam(required = false) LocalDate start,
            @RequestParam(required = false) LocalDate end,
            @RequestParam(required = false) Integer hotelId,
            @RequestParam(required = false) Integer roomId,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer managerId) {
        return bookingService.getBookings(start, end, hotelId, roomId, userId, managerId);
    }

    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody Booking booking) {
        return bookingService.createBooking(booking);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBookingById(@PathVariable Integer id) {
        return bookingService.getBookingById(id);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteBooking(
            @RequestParam(required = false) Integer hotelId,
            @RequestParam(required = false) Integer managerId,
            @RequestParam(required = false) Integer userId) {
        return bookingService.deleteBookings(hotelId, managerId, userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBooking(@PathVariable Integer id) {
        return bookingService.deleteBooking(id);
    }
}
