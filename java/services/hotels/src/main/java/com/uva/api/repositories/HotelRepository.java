package com.uva.api.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uva.api.models.Hotel;

public interface HotelRepository extends JpaRepository<Hotel, Integer> {
    List<Hotel> findAllByManagerId(Integer managerId);
}
