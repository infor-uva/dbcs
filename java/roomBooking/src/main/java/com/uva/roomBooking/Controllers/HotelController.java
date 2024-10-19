package com.uva.roomBooking.Controllers;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

import com.uva.roomBooking.Exceptions.HotelNotFoundException;
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
    public ResponseEntity<List<Hotel>> getAllHotels() {
        List<Hotel> hotels = hotelRepository.findAll();
        return new ResponseEntity<>(hotels, HttpStatus.OK);
    }

    // Añadir un hotel con sus habitaciones
    @PostMapping
    public ResponseEntity<Hotel> addHotel(@RequestBody Hotel hotel) {
        if (hotel.getRooms() != null) {
            hotel.getRooms().forEach(room -> room.setHotel(hotel));  // Aseguramos la relación bidireccional
        }
        Hotel savedHotel = hotelRepository.save(hotel);
        return new ResponseEntity<>(savedHotel, HttpStatus.CREATED);
    }

    // Obtener un hotel por su ID
    @GetMapping("/{id}")
    public ResponseEntity<Hotel> getHotelById(@PathVariable int id) {
        return hotelRepository.findById(id)
            .map(value -> new ResponseEntity<>(value, HttpStatus.OK))
            .orElseThrow(() -> new HotelNotFoundException(id));
    }

    // Borrar un hotel junto con sus habitaciones (borrado en cascada)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHotel(@PathVariable Integer id) {
        Optional<Hotel> target = hotelRepository.findById(id);
        if (target.isPresent()) {
            // Borramos habitaciones asociadas
            roomRepository.deleteAllByHotelId(id);
            hotelRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        throw new HotelNotFoundException(id); // Lanzamos excepción personalizada
    }

    // Obtener habitaciones de un hotel según disponibilidad y fechas
    @GetMapping("/{hotelId}/rooms")
    public ResponseEntity<List<Room>> getRoomsFromHotel(
        @PathVariable int hotelId,
        @RequestParam(required = false) LocalDate date1,
        @RequestParam(required = false) LocalDate date2) {

        List<Room> rooms;
        if (date1 != null && date2 != null) {
            rooms = roomRepository.findAvailableRoomsByHotelAndDates(hotelId, date1, date2);
        } else {
            rooms = roomRepository.findByHotelId(hotelId);
        }
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    // Actualizar disponibilidad de habitaciones de un hotel
    @PatchMapping("/{hotelId}/rooms")
    public ResponseEntity<List<Room>> updateRoomAvailabilityFromHotel(
        @PathVariable int hotelId, @RequestBody List<Room> updatedRooms) {

        List<Room> existingRooms = roomRepository.findByHotelId(hotelId);
        for (Room updatedRoom : updatedRooms) {
            existingRooms.stream()
                .filter(room -> Integer.valueOf(room.getId()).equals(updatedRoom.getId())) // Conversión a Integer
                .findFirst()
                .ifPresent(room -> room.setAvailable(updatedRoom.isAvailable()));
        }
        roomRepository.saveAll(existingRooms);
        return new ResponseEntity<>(existingRooms, HttpStatus.OK);
    }

    // Obtener los detalles de una habitación específica en un hotel
    @GetMapping("/{hotelId}/rooms/{roomId}")
    public ResponseEntity<Room> getRoomByIdFromHotel(
        @PathVariable int hotelId, @PathVariable int roomId) {

        Optional<Room> room = roomRepository.findByHotelIdAndRoomId(hotelId, roomId);
        return room.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                   .orElseThrow(() -> new HotelNotFoundException(hotelId)); // Lanzamos excepción personalizada
    }
}
