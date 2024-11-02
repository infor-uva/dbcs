package com.uva.roomBooking.Repositories;

import com.uva.roomBooking.Models.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Integer> {

    Optional<Room> findByIdAndHotelId(int id, int hotelId);

    // Encontrar todas las habitaciones de un hotel
    List<Room> findAllByHotelId(int hotelId);

    // Encontrar habitaciones disponibles de un hotel en un rango de fechas
    @Query("""
                SELECT r FROM Room r
                WHERE r.hotel.id = ?1
                AND r.available = true
                AND NOT EXISTS (
                    SELECT b FROM Booking b
                    WHERE b.roomId.id = r.id
                    AND (
                        b.endDate > ?2
                        OR
                        b.startDate > ?3
                    )
                )
            """)
    List<Room> findAvailableRoomsByHotelAndDates(
            int hotelId, LocalDate startDate, LocalDate endDate);
}
