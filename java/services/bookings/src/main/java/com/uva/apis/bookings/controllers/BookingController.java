package com.uva.apis.bookings.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uva.apis.bookings.models.Booking;
import com.uva.apis.bookings.services.BookingService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @GetMapping
    public List<Booking> getAllBookings(
            @RequestParam(required = false) LocalDate start,
            @RequestParam(required = false) LocalDate end,
            @RequestParam(required = false) Integer hotelId,
            @RequestParam(required = false) Integer roomId,
            @RequestParam(required = false) Integer userId) {
        return bookingService.getBookings(start, end, hotelId, roomId, userId);
    }

    @PostMapping
    public Booking createBooking(@RequestBody Booking booking) {
        return bookingService.createBooking(booking);
    }

    @GetMapping("/{id}")
    public Booking getBookingById(@PathVariable Integer id) {
        return bookingService.getBookingById(id);
    }

    @DeleteMapping /// ?hotelId={hotelId}|managerId={managerId}
    public ResponseEntity<?> deleteBooking(
            @RequestParam(required = false) Integer hotelId,
            @RequestParam(required = false) Integer managerId) {
        try {
            List<Booking> bookings;
            String message;
            if (managerId != null) {
                bookings = bookingService.deleteAllByManagerId(managerId);
                message = "No bookings for this manager";
            } else if (hotelId != null) {
                bookings = bookingService.deleteBookingsByHotelId(hotelId);
                message = "No bookings for this hotel";
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return !bookings.isEmpty()
                    ? new ResponseEntity<>(bookings, HttpStatus.OK)
                    : new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            e.printStackTrace(System.err);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Integer id) {
        try {
            bookingService.deleteBooking(id);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
