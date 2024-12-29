package com.uva.api.bookings.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uva.api.bookings.models.Booking;
import com.uva.api.bookings.services.BookingService;
import com.uva.api.bookings.utils.Utils;

import java.time.LocalDate;

@RestController
@RequestMapping("/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @GetMapping
    public ResponseEntity<?> getAllBookings(
            @RequestHeader(value = "Authorization", required = true) String authorization,
            @RequestParam(required = false) LocalDate start,
            @RequestParam(required = false) LocalDate end,
            @RequestParam(required = false) Integer hotelId,
            @RequestParam(required = false) Integer roomId,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer managerId) {
        String token = Utils.getToken(authorization);
        return bookingService.getBookings(token, start, end, hotelId, roomId, userId, managerId);
    }

    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody Booking booking) {
        return bookingService.createBooking(booking);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteBooking(
            @RequestParam(required = false) Integer hotelId,
            @RequestParam(required = false) Integer managerId,
            @RequestParam(required = false) Integer userId) {
        return bookingService.deleteBookings(hotelId, managerId, userId);
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<?> getBookingById(
            @RequestHeader(value = "Authorization", required = true) String authorization,
            @PathVariable Integer id) {
        String token = Utils.getToken(authorization);
        return bookingService.getBookingById(token, id);
    }

    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<?> deleteBooking(
            @RequestHeader(value = "Authorization", required = true) String authorization,
            @PathVariable Integer id) {
        String token = Utils.getToken(authorization);
        return bookingService.deleteBooking(token, id);
    }
}
