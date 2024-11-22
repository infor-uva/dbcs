package com.uva.roomBooking.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uva.roomBooking.models.Hotel;

public interface HotelRepository extends JpaRepository<Hotel, Integer> {

}
