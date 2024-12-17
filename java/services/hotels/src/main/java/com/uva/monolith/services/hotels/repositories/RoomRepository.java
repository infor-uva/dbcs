package com.uva.monolith.services.hotels.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.uva.monolith.services.hotels.models.Room;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Integer> {

    Optional<Room> findByIdAndHotelId(int id, int hotelId);

    // Encontrar todas las habitaciones de un hotel
    List<Room> findAllByHotelId(int hotelId);
}
