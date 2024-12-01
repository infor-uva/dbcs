package com.uva.authentication.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.uva.authentication.models.remote.HotelManager;

public interface HotelManagerRepository extends JpaRepository<HotelManager, Integer> {
  Optional<HotelManager> findByEmail(String email);
}