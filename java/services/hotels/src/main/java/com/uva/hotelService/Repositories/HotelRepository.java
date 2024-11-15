package com.uva.hotelService.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uva.hotelService.Models.Hotel;

public interface HotelRepository extends JpaRepository<Hotel, Integer> {

}
