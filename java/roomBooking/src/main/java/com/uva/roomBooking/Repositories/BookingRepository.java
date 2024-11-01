// BookingRepository.java
package com.uva.roomBooking.Repositories;

import com.uva.roomBooking.Models.Booking;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    
    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId AND b.startDate < :endDate AND b.endDate > :startDate")
    List<Booking> findByRoomIdAndDateRange(@Param("roomId") int roomId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
