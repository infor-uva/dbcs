// BookingRepository.java
package com.uva.bookings.Repositories;

import com.uva.bookings.Models.Booking;

import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Query("SELECT b FROM Booking b WHERE b.roomId = ?1 AND b.startDate < ?2 AND b.endDate > ?3")
    List<Booking> findByRoomIdAndDateRange(@Param("roomId") int roomId, @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Transactional
    @Modifying
    @Query("DELETE FROM Booking b WHERE b.id = ?1")
    void deleteBookingById(@Param("id") Integer id);


    List<Booking> findByUserId(Integer userId);

    @Query("SELECT b FROM Booking b WHERE "
     + "(:roomId IS NULL OR b.roomId = :roomId) AND "
     + "(:startDate IS NULL OR b.startDate >= :startDate) AND "
     + "(:endDate IS NULL OR b.endDate <= :endDate)")
    List<Booking> findByFilters(@Param("startDate") String startDate, 
                            @Param("endDate") String endDate, 
                            @Param("roomId") Integer roomId);



}
