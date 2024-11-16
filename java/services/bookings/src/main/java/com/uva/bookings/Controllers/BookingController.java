package com.uva.bookings.Controllers;

import com.uva.bookings.Models.Booking;
import com.uva.bookings.Repositories.BookingRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping
    public ResponseEntity<List<Booking>> getBookings(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Integer roomId) {
    
        // Si no se proporcionan filtros, devolver todas las reservas
        if (startDate == null && endDate == null && roomId == null) {
            return ResponseEntity.ok(bookingRepository.findAll());
        }
    
        // Obtener reservas filtradas por los parámetros dados
        List<Booking> bookings = bookingRepository.findByFilters(startDate, endDate, roomId);
        if (bookings.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
    
        return ResponseEntity.ok(bookings);
    }    

    @PostMapping
    public Booking createBooking(@RequestBody Booking booking) {
        // Llamada al microservicio de usuarios para validar al usuario
        ResponseEntity<Void> userResponse = restTemplate.exchange(
            "http://user-service/users/{userId}",
            HttpMethod.GET,
            null,
            Void.class,
            booking.getUserId()
        );
        if (!userResponse.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("User not found");
        }

        // Llamada al microservicio de habitaciones para validar la habitación
        ResponseEntity<Void> roomResponse = restTemplate.exchange(
            "http://room-service/rooms/{roomId}",
            HttpMethod.GET,
            null,
            Void.class,
            booking.getRoomId()
        );
        if (!roomResponse.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Room not found");
        }

        // Verificar disponibilidad
        List<Booking> existingBookings = bookingRepository.findByRoomIdAndDateRange(
                booking.getRoomId(), booking.getStartDate(), booking.getEndDate());

        if (!existingBookings.isEmpty()) {
            throw new RuntimeException("Room is not available for the selected dates");
        }

        return bookingRepository.save(booking);
    }

    @GetMapping("/{id}")
    public Booking getBookingById(@PathVariable Integer id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    @GetMapping(params = "userId")
    public ResponseEntity<List<Booking>> getBookingsByUserId(@RequestParam Integer userId) {
        // Llamada al microservicio de usuarios para validar la existencia del usuario
        ResponseEntity<Void> userResponse = restTemplate.exchange(
            "http://user-service/users/{userId}",
            HttpMethod.GET,
            null,
            Void.class,
            userId
        );

        if (!userResponse.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.noContent().build();
        }

        List<Booking> bookings = bookingRepository.findByUserId(userId);
        if (bookings.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(bookings);
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
