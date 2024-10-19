package com.uva.roomBooking.Repositories;

import com.uva.roomBooking.Models.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Integer> {

    // Encontrar todas las habitaciones de un hotel
    List<Room> findByHotelId(int hotelId);

    // Borrar todas las habitaciones asociadas a un hotel
    @Transactional
    @Modifying
    void deleteAllByHotelId(int hotelId);

    // Encontrar habitaciones disponibles de un hotel en un rango de fechas
    @Query("""
    SELECT r FROM Room r 
    WHERE r.hotel.id = :hotelId 
    AND r.available = true 
    AND NOT EXISTS (
        SELECT b FROM Booking b 
        WHERE b.roomID.id = r.id 
        AND (b.startDate <= :endDate AND b.endDate >= :startDate)
    )
""")
    List<Room> findAvailableRoomsByHotelAndDates(
        int hotelId, LocalDate startDate, LocalDate endDate);

    // Encontrar una habitación específica por hotel y su ID
    @Query("SELECT r FROM Room r WHERE r.hotel.id = :hotelId AND r.id = :roomId")
    Optional<Room> findByHotelIdAndRoomId(int hotelId, int roomId);
}
