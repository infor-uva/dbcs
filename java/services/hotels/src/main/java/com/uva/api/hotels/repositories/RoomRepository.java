package com.uva.api.hotels.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.uva.api.hotels.models.Room;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Integer> {

    Optional<Room> findByIdAndHotelId(int id, int hotelId);

    // Encontrar todas las habitaciones de un hotel
    List<Room> findAllByHotelId(int hotelId);
}
