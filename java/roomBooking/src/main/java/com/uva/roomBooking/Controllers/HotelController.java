package com.uva.roomBooking.Controllers;

import java.util.List;
import java.time.LocalDate;

import com.uva.roomBooking.Models.Hotel;
import com.uva.roomBooking.Models.Room;
import com.uva.roomBooking.Repositories.HotelRepository;
import com.uva.roomBooking.Repositories.RoomRepository;

import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

// TODO incluir excepciones y procesado de respuestas con entidad y código de estado

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

  @GetMapping
  public List<Hotel> getAllHotels() {
    return hotelRepository.findAll();
  }

  @PostMapping
  public Hotel addHotel(@RequestBody Hotel hotel) {
    // TODO comprobar y asegurar que se agregan todas sus habitaciones también
    return hotelRepository.save(hotel);
  }

  @GetMapping("/{id}")
  public Hotel getHotelById(@PathVariable int id) {
    return hotelRepository.findById(id).orElse(null);
  }

  @DeleteMapping("/{id}")
  public Hotel deleteHotel(@PathVariable Integer id) {
    Hotel target;
    if ((target = hotelRepository.findById(id).orElse(null)) != null) {
      // TODO asegurarse de que el borrado es en CASCADA -> Hotel y habitaciones
      hotelRepository.deleteById(id);
    }
    return target;
  }

  // TODO completar controllers de rooms
  @GetMapping("/{id}/rooms")
  // Dejo un esqueleto, a lo mejor sería mejor otra forma de plantear el manejo y
  // recogida de query params
  public List<Room> getRoomsFromHotel(@PathVariable int hotelId, @RequestParam LocalDate date1,
      @RequestParam LocalDate date2) {
    throw new IllegalStateException("Not implemented yet");
  }

  @PatchMapping("/{id}/rooms")
  public List<Room> updateRoomAvailabilityFromHotel(@PathVariable int hotelId) {
    throw new IllegalStateException("Not implemented yet");
  }

  @GetMapping("/{hotelId}/rooms/{roomId}")
  public List<Room> getRoomByIdFromHotel(@PathVariable int hotelId, @PathVariable int roomId) {
    throw new IllegalStateException("Not implemented yet");
  }

  // Te dejo esto de la otra clase comentado por si te sirve algo

  // @GetMapping
  // public List<Room> getAllRooms() {
  // return roomRepository.findAll();
  // }

  // @PostMapping
  // public Room addRoom(@RequestBody Room room) {
  // return roomRepository.save(room);
  // }

  // @DeleteMapping("/{id}")
  // public void deleteRoom(@PathVariable Integer id) {
  // roomRepository.deleteById(id);
  // }

}