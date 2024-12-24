package com.uva.monolith.controllers;

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
import com.uva.monolith.models.Hotel;
import com.uva.monolith.models.Room;
import com.uva.monolith.repositories.HotelRepository;
import com.uva.monolith.repositories.RoomRepository;
import com.uva.monolith.services.HotelService;

@RestController
@RequestMapping("hotels")
@CrossOrigin(origins = "*")
public class HotelController {
    @Autowired
    private HotelService hotelService;

    @GetMapping
    public List<Hotel> getAllHotels(
            @RequestParam(required = false) Integer managerId,
            @RequestParam(required = false) LocalDate start,
            @RequestParam(required = false) LocalDate end) {
        return hotelService.getAllHotels(managerId, start, end);
    }

    @PostMapping
    public ResponseEntity<?> addHotel(@RequestBody Hotel hotel) {
        Hotel savedHotel = hotelService.addHotel(hotel);
        return new ResponseEntity<>(savedHotel, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public Hotel getHotelById(@PathVariable int id) {
        return hotelService.getHotelById(id);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteHotelsByManagerId(
            @RequestParam(required = true) Integer managerId) {
        List<Hotel> hotels = hotelService.deleteHotelsByManagerId(managerId);
        return new ResponseEntity<>(hotels, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteHotel(@PathVariable Integer id) {
        hotelService.deleteHotel(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{hotelId}/rooms")
    public ResponseEntity<List<Room>> getRoomsFromHotel(
            @PathVariable int hotelId,
            @RequestParam(required = false) LocalDate start,
            @RequestParam(required = false) LocalDate end) {
        List<Room> rooms = hotelService.getRoomsFromHotel(hotelId, start, end);
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    @PatchMapping("/{hotelId}/rooms/{roomId}")
    public ResponseEntity<Room> updateRoomAvailability(
            @PathVariable int hotelId,
            @PathVariable int roomId,
            @RequestBody Map<String, Boolean> body) {
        if (!body.containsKey("available")) {
            throw new InvalidRequestException("El campo 'available' es obligatorio");
        }
        Room updatedRoom = hotelService.updateRoomAvailability(hotelId, roomId, body.get("available"));
        return new ResponseEntity<>(updatedRoom, HttpStatus.OK);
    }

    @GetMapping("/{hotelId}/rooms/{roomId}")
    public Room getRoomByIdFromHotel(
            @PathVariable int hotelId, @PathVariable int roomId) {
        return hotelService.getRoomByIdFromHotel(hotelId, roomId);
    }
}
