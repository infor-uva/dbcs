package com.uva.api.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.uva.api.models.Client;

public interface ClientRepository extends JpaRepository<Client, Integer> {
  Optional<Client> findByEmail(String email);
}