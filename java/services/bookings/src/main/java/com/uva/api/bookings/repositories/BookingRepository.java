// BookingRepository.java
package com.uva.api.bookings.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uva.api.bookings.models.Booking;

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

        @Query("SELECT EXISTS (Select b from Booking b where b.userId = ?1 AND b.end >= ?2)")
        boolean existsActiveByUserIdAndDate(int userId, LocalDate date);

        @Query("SELECT EXISTS (Select b from Booking b where b.userId = ?1 AND b.end < ?2)")
        boolean existsInactiveByUserIdAndDate(int userId, LocalDate date);

        @Transactional
        void deleteAllByUserId(int userId);

        @Transactional
        void deleteAllByManagerId(int managerId);

        @Query("SELECT b from Booking b where b.end = ?1")
        List<Booking> findAllPassed(LocalDate yesterday);
        // List<Booking> findAllByEnd(LocalDate end);// también puede ser

}
