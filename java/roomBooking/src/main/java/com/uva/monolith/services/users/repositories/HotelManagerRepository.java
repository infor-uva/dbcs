package com.uva.monolith.services.users.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.uva.monolith.services.users.models.HotelManager;

public interface HotelManagerRepository extends JpaRepository<HotelManager, Integer> {
  Optional<HotelManager> findByEmail(String email);
}