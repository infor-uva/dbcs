// BookingRepository.java
package com.uva.apis.bookings.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uva.apis.bookings.models.Booking;

import jakarta.transaction.Transactional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

        // Bookings
        @Query("SELECT b FROM Booking b WHERE ?1 <= b.end AND ?2 >= b.start")
        List<Booking> findAllInDateRange(
                        @Param("start") LocalDate start,
                        @Param("end") LocalDate end);

        void deleteById(int id);

        // Hotels
        List<Booking> findAllByRoomId(int roomId);

        @Query("SELECT b FROM Booking b WHERE b.roomId = ?1 AND ?2 <= b.end AND ?3 >= b.start")
        List<Booking> findAllByRoomIdInDateRange(
                        @Param("roomId") int roomId,
                        @Param("start") LocalDate start,
                        @Param("end") LocalDate end);

        List<Booking> findAllByHotelId(Integer roomId);

        @Transactional
        void deleteAllByHotelId(int hotelId);

        // Users (Clients or Managers)
        List<Booking> findAllByUserId(int userId);

        List<Booking> findAllByManagerId(int managerId);

        @Transactional
        void deleteAllByUserId(int userId);

        @Transactional
        void deleteAllByManagerId(int managerId);
}
