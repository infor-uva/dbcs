package com.uva.monolith.services.hotels.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.uva.monolith.services.hotels.models.Hotel;

public interface HotelRepository extends JpaRepository<Hotel, Integer> {
    List<Hotel> findAllByManagerId(Integer managerId);
}
