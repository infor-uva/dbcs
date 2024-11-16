package com.uva.hotelService.Controllers;

import java.util.List;
import java.util.Map;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.uva.hotelService.Exceptions.HotelNotFoundException;
import com.uva.hotelService.Exceptions.InvalidDateRangeException;
import com.uva.hotelService.Exceptions.InvalidRequestException;
import com.uva.hotelService.Models.Hotel;
import com.uva.hotelService.Models.Room;
import com.uva.hotelService.Repositories.HotelRepository;
import com.uva.hotelService.Repositories.RoomRepository;

@RestController
@RequestMapping("hotels")
@CrossOrigin(origins = "*")
public class HotelController {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RestTemplate restTemplate;

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
    @Transactional
    public ResponseEntity<Void> deleteHotel(@PathVariable Integer id) {
        Hotel target = hotelRepository.findById(id)
                .orElseThrow(() -> new HotelNotFoundException(id));
        // bookingRepository.deleteAllByHotelId(id);
        // TODO revisar como se va a hacer cuando se haga lo de reservas
        String deleteUrl = String.format("http://reservas-service/?hotelId=%d", target.getId());
        restTemplate.delete(deleteUrl);
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
        rooms = roomRepository.findAllByHotelIdAndAvailable(hotelId, true);
        if (start != null && end != null) {
            if (!start.isBefore(end)) {
                throw new InvalidDateRangeException("La fecha de inicio debe ser anterior a la fecha de fin");
            }
            rooms = rooms.stream().filter(
                    room -> {
                        // TODO revisar
                        String url = String.format("http://reservas-service/?hotelId=%d&start=%s&end=%s", room.getId(),
                                start, end);
                        return Boolean.parseBoolean(
                                restTemplate.exchange(
                                        url,
                                        HttpMethod.GET,
                                        null,
                                        Boolean.class).toString());
                    }).toList();

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
