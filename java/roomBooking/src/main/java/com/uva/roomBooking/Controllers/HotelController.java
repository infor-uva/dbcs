package com.uva.roomBooking.Controllers;

import java.util.List;
import java.util.Map;
import java.time.LocalDate;

import com.uva.roomBooking.Exceptions.HotelNotFoundException;
import com.uva.roomBooking.Exceptions.InvalidDateRangeException;
import com.uva.roomBooking.Exceptions.InvalidRequestException;
import com.uva.roomBooking.Models.Hotel;
import com.uva.roomBooking.Models.Room;
import com.uva.roomBooking.Repositories.HotelRepository;
import com.uva.roomBooking.Repositories.RoomRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("hotels")
@CrossOrigin(origins = "*")
public class HotelController {
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;

    public HotelController(HotelRepository hotelRepository, RoomRepository roomRepository) {
        this.hotelRepository = hotelRepository;
        this.roomRepository = roomRepository;
    }

    // Obtener todos los hoteles
    @GetMapping
    public List<Hotel> getAllHotels() {
        return hotelRepository.findAll();
    }

    // Añadir un hotel con sus habitaciones
    @PostMapping
    public ResponseEntity<Hotel> addHotel(@RequestBody Hotel hotel) {
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
    public ResponseEntity<Void> deleteHotel(@PathVariable Integer id) {
        Hotel target = hotelRepository.findById(id)
                .orElseThrow(() -> new HotelNotFoundException(id));
        hotelRepository.delete(target);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Obtener habitaciones de un hotel según disponibilidad y fechas
    @GetMapping("/{hotelId}/rooms")
    public ResponseEntity<List<Room>> getRoomsFromHotel(
            @PathVariable int hotelId,
            @RequestParam(required = false) LocalDate date1,
            @RequestParam(required = false) LocalDate date2) {

        List<Room> rooms;
        if (date1 != null && date2 != null) {
            if (!date1.isBefore(date2)) {
                throw new InvalidDateRangeException("La fecha de inicio debe ser anterior a la fecha de fin");
            }
            rooms = roomRepository.findAvailableRoomsByHotelAndDates(hotelId, date1, date2);
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
