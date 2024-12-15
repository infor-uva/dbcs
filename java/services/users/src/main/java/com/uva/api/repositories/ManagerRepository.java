package com.uva.api.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.uva.api.models.Manager;

public interface ManagerRepository extends JpaRepository<Manager, Integer> {
  Optional<Manager> findByEmail(String email);
}