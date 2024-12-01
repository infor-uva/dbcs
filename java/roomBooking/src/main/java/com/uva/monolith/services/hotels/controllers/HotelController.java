package com.uva.monolith.services.hotels.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.uva.monolith.exceptions.HotelNotFoundException;
import com.uva.monolith.exceptions.InvalidDateRangeException;
import com.uva.monolith.exceptions.InvalidRequestException;
import com.uva.monolith.services.bookings.repositories.BookingRepository;
import com.uva.monolith.services.hotels.models.Hotel;
import com.uva.monolith.services.hotels.models.Room;
import com.uva.monolith.services.hotels.repositories.HotelRepository;
import com.uva.monolith.services.hotels.repositories.RoomRepository;
import com.uva.monolith.services.users.models.HotelManager;
import com.uva.monolith.services.users.repositories.HotelManagerRepository;

@RestController
@RequestMapping("hotels")
@CrossOrigin(origins = "*")
public class HotelController {
    @Autowired
    private HotelManagerRepository hotelManagerRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    public HotelController(HotelRepository hotelRepository, RoomRepository roomRepository,
            BookingRepository bookingRepository) {
        this.hotelRepository = hotelRepository;
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
    }

    // Obtener todos los hoteles
    @GetMapping
    public List<Hotel> getAllHotels() {
        return hotelRepository.findAll();
    }

    // Añadir un hotel con sus habitaciones
    @PostMapping
    public ResponseEntity<Hotel> addHotel(@RequestBody Hotel hotel) {
        Optional<HotelManager> hm = hotelManagerRepository.findById(hotel.getHotelManager().getId());
        if (!hm.isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        hotel.setHotelManager(hm.get());
        Hotel savedHotel = hotelRepository.save(hotel);
        return new ResponseEntity<>(savedHotel, HttpStatus.CREATED);
    }

    // Obtener un hotel por su ID
    @GetMapping("/{id}")
    public Hotel getHotelById(@PathVariable int id) {
        return hotelRepository.findById(id)
                .orElseThrow(() -> new HotelNotFoundException(id));
    }

    // Borrar un hotel junto con sus habitaciones (borrado en cascada)
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteHotel(@PathVariable Integer id) {
        Hotel target = hotelRepository.findById(id)
                .orElseThrow(() -> new HotelNotFoundException(id));
        bookingRepository.deleteAllByHotelId(id);
        HotelManager hm = target.getHotelManager();
        hm.getHotels().removeIf(h -> h.getId() == target.getId());
        hotelManagerRepository.save(hm);
        bookingRepository.flush();
        hotelRepository.delete(target);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Obtener habitaciones de un hotel según disponibilidad y fechas
    @GetMapping("/{hotelId}/rooms")
    public ResponseEntity<List<Room>> getRoomsFromHotel(
            @PathVariable int hotelId,
            @RequestParam(required = false) LocalDate start,
            @RequestParam(required = false) LocalDate end) {

        List<Room> rooms;
        if (start != null && end != null) {
            if (!start.isBefore(end)) {
                throw new InvalidDateRangeException("La fecha de inicio debe ser anterior a la fecha de fin");
            }
            rooms = roomRepository.findAvailableRoomsByHotelAndDates(hotelId, start, end);
        } else {
            rooms = roomRepository.findAllByHotelId(hotelId);
        }
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    // Actualizar disponibilidad de una habitación específica en un hotel
    @PatchMapping("/{hotelId}/rooms/{roomId}")
    public ResponseEntity<Room> updateRoomAvailability(
            @PathVariable int hotelId,
            @PathVariable int roomId,
            @RequestBody Map<String, Boolean> body) {

        if (!body.containsKey("available")) {
            throw new InvalidRequestException("El campo 'available' es obligatorio");
        }

        Room targetRoom = roomRepository.findByIdAndHotelId(roomId, hotelId)
                .orElseThrow(() -> new IllegalArgumentException("Habitación no encontrada"));

        targetRoom.setAvailable(body.get("available"));
        roomRepository.save(targetRoom);

        return new ResponseEntity<>(targetRoom, HttpStatus.OK);
    }

    // Obtener los detalles de una habitación específica en un hotel
    @GetMapping("/{hotelId}/rooms/{roomId}")
    public Room getRoomByIdFromHotel(
            @PathVariable int hotelId, @PathVariable int roomId) {
        return roomRepository.findByIdAndHotelId(roomId, hotelId)
                .orElseThrow(() -> new HotelNotFoundException(hotelId));
    }
}
