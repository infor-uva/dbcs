package com.uva.api.hotels.controllers;

import java.util.Map;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.uva.api.hotels.exceptions.InvalidRequestException;
import com.uva.api.hotels.models.Hotel;
import com.uva.api.hotels.services.HotelService;
import com.uva.api.hotels.utils.Utils;

@RestController
@RequestMapping("hotels")
@CrossOrigin(origins = "*")
public class HotelController {
    @Autowired
    private HotelService hotelService;

    @GetMapping
    public ResponseEntity<?> getAllHotels(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam(required = false) Integer managerId,
            @RequestParam(required = false) LocalDate start,
            @RequestParam(required = false) LocalDate end) {
        String token = Utils.getTokenSoft(authorization);
        return hotelService.getAllHotels(token, managerId, start, end);
    }

    @PostMapping
    public ResponseEntity<?> addHotel(@RequestBody Hotel hotel) {
        return hotelService.addHotel(hotel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getHotelById(
            @RequestHeader(value = "Authorization", required = true) String authorization,
            @PathVariable int id) {
        String token = Utils.getToken(authorization);
        return hotelService.getHotelById(token, id);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteHotelsByManagerId(
            @RequestHeader(value = "Authorization", required = true) String authorization,
            @RequestParam(required = true) Integer managerId) {
        System.out.println(authorization);
        String token = Utils.getToken(authorization);
        return hotelService.deleteHotelsByManagerId(token, managerId);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> deleteHotel(
            @RequestHeader(value = "Authorization", required = true) String authorization,
            @PathVariable Integer id) {
        String token = Utils.getToken(authorization);
        return hotelService.deleteHotel(token, id);
    }

    @GetMapping("/{hotelId}/rooms")
    public ResponseEntity<?> getRoomsFromHotel(
            @PathVariable int hotelId,
            @RequestParam(required = false) LocalDate start,
            @RequestParam(required = false) LocalDate end) {
        return hotelService.getRoomsFromHotel(hotelId, start, end);
    }

    @PatchMapping("/{hotelId}/rooms/{roomId}")
    public ResponseEntity<?> updateRoomAvailability(
            @RequestHeader(value = "Authorization", required = true) String authorization,
            @PathVariable int hotelId,
            @PathVariable int roomId,
            @RequestBody Map<String, Boolean> body) {
        String token = Utils.getToken(authorization);
        if (!body.containsKey("available")) {
            throw new InvalidRequestException("El campo 'available' es obligatorio");
        }
        return hotelService.updateRoomAvailability(token, hotelId, roomId, body.get("available"));
    }

    @GetMapping("/{hotelId}/rooms/{roomId}")
    public ResponseEntity<?> getRoomByIdFromHotel(@PathVariable int hotelId, @PathVariable int roomId) {
        return hotelService.getRoomByIdFromHotel(hotelId, roomId);
    }
}
