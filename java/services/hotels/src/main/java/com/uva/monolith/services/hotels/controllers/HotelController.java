package com.uva.monolith.services.hotels.controllers;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.uva.monolith.api.BookingAPI;
import com.uva.monolith.api.ManagerAPI;
import com.uva.monolith.exceptions.HotelNotFoundException;
import com.uva.monolith.exceptions.InvalidDateRangeException;
import com.uva.monolith.exceptions.InvalidRequestException;
import com.uva.monolith.services.hotels.models.Hotel;
import com.uva.monolith.services.hotels.models.Room;
import com.uva.monolith.services.hotels.repositories.HotelRepository;
import com.uva.monolith.services.hotels.repositories.RoomRepository;

@RestController
@RequestMapping("hotels")
@CrossOrigin(origins = "*")
public class HotelController {
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private BookingAPI bookingAPI;
    @Autowired
    private ManagerAPI managerAPI;

    // Obtener todos los hoteles
    @GetMapping
    public List<Hotel> getAllHotels(
            @RequestParam(required = false) Integer managerId,
            @RequestParam(required = false) LocalDate start,
            @RequestParam(required = false) LocalDate end) {
        List<Hotel> hotels = (managerId != null)
                ? hotelRepository.findAllByManagerId(managerId)
                : hotelRepository.findAll();
        if (start != null && end != null) {
            // Filtramos para los hoteles que
            // tengan habitaciones disponibles para ese rango de fechas
            Set<Integer> notAvailableRoomsId = bookingAPI.getNotAvailableRooms(start, end);
            hotels = hotels.stream().map(h -> {
                if (h.getRooms().size() == 0)
                    return h;
                List<Room> rooms = h.getRooms().stream()
                        .filter(r -> notAvailableRoomsId.contains(r.getId())).toList();
                h.setRooms(rooms);
                return h;
            }).filter(h -> h.getRooms().size() >= 0).toList();
        }
        return hotels;
    }

    // Añadir un hotel con sus habitaciones
    @PostMapping
    public ResponseEntity<?> addHotel(@RequestBody Hotel hotel) {
        try {

            boolean exist = managerAPI.existsManagerById(hotel.getManagerId());
            String message = "No existe el manager con id " + String.valueOf(hotel.getManagerId());
            if (!exist) {
                return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
            }
            Hotel savedHotel = hotelRepository.save(hotel);
            return new ResponseEntity<>(savedHotel, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw e;
        }
    }

    // Obtener un hotel por su ID
    @GetMapping("/{id}")
    public Hotel getHotelById(@PathVariable int id) {
        return hotelRepository.findById(id)
                .orElseThrow(() -> new HotelNotFoundException(id));
    }

    // Borrar hoteles administrados por un manager concreto
    @DeleteMapping
    public ResponseEntity<?> deleteHotelsByManagerId(
            @RequestParam(required = true) Integer managerId) {
        List<Hotel> hotels = hotelRepository.findAllByManagerId(managerId);
        if (hotels.isEmpty()) {
            return new ResponseEntity<>("No hay hoteles para el manager con id " + managerId, HttpStatus.BAD_REQUEST);
        }
        bookingAPI.deleteAllByManagerId(managerId);
        hotelRepository.deleteAll(hotels);
        return new ResponseEntity<>(hotels, HttpStatus.OK);
    }

    // Borrar un hotel junto con sus habitaciones (borrado en cascada)
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteHotel(@PathVariable Integer id) {
        Hotel target = hotelRepository.findById(id)
                .orElseThrow(() -> new HotelNotFoundException(id));
        bookingAPI.deleteAllByHotelId(id);
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
        boolean dateMode = start != null && end != null;
        if (dateMode) {
            if (!start.isBefore(end)) {
                throw new InvalidDateRangeException("La fecha de inicio debe ser anterior a la fecha de fin");
            }
        }
        rooms = roomRepository.findAllByHotelId(hotelId);

        if (dateMode) {
            // Consultar el set de ids ocupados del id
            Set<Integer> notAvailableRoomsId = bookingAPI.getNotAvailableRooms(hotelId, start, end);
            rooms = rooms.stream()
                    .filter(r -> notAvailableRoomsId.contains(r.getId())).toList();
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
