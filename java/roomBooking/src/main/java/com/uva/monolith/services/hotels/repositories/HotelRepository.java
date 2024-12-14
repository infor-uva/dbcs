package com.uva.api.services.hotels.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.uva.api.services.hotels.models.Hotel;

public interface HotelRepository extends JpaRepository<Hotel, Integer> {
    @Query("SELECT h FROM Hotel h WHERE h.hotelManager.id = ?1")
    List<Hotel> findAllByHotelManager(Integer hotelManager);
}
