// BookingRepository.java
package com.uva.roomBooking.Repositories;

import com.uva.roomBooking.Models.Booking;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Query("SELECT b FROM Booking b WHERE b.roomId.id = ?1 AND b.startDate < ?2 AND b.endDate > ?3")
    List<Booking> findByRoomIdAndDateRange(@Param("roomId") int roomId, @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Modifying
    @Query("DELETE FROM Booking b WHERE b.id = :id")
    void deleteBookingById(@Param("id") Integer id);

    
}
