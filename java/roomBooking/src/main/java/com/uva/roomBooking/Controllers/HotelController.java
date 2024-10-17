package com.uva.roomBooking.Controllers;

import com.uva.roomBooking.Models.Hotel;
import com.uva.roomBooking.Repositories.HotelRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("hotels")
@CrossOrigin(origins = "*")
public class HotelController {
  private final HotelRepository repository;

  public HotelController(HotelRepository repository) {
    this.repository = repository;
  }

  @GetMapping
  public List<Hotel> getAllRooms() {
    return repository.findAll();
  }

  @PostMapping
  public Hotel addRoom(@RequestBody Hotel hotel) {
    return repository.save(hotel);
  }

  @DeleteMapping("/{id}")
  public Hotel deleteRoom(@PathVariable Integer id) {
    Hotel target;
    if ((target = repository.findById(id).orElse(null)) != null) {
      repository.deleteById(id);
    }
    return target;
  }

}
