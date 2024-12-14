// BookingRepository.java
package com.uva.apis.bookings.repositories;

import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uva.apis.bookings.models.Booking;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
        @Query("SELECT b FROM Booking b WHERE b.userId.id = ?1")
        List<Booking> findByUserId(int userId);

        @Query("SELECT b FROM Booking b WHERE b.roomId.id = ?1")
        List<Booking> findByRoomId(int roomId);

        @Query("SELECT b FROM Booking b WHERE b.startDate >= ?1 AND b.endDate <= ?2")
        List<Booking> findByDateRange(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        @Query("SELECT b FROM Booking b WHERE b.roomId.id = ?1 AND b.startDate < ?2 AND b.endDate > ?3")
        List<Booking> findByRoomIdAndDateRange(@Param("roomId") int roomId, @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        @Transactional
        @Modifying
        void deleteById(int id);

        @Transactional
        @Modifying
        void deleteAllByHotelId(int hotelId);

}
