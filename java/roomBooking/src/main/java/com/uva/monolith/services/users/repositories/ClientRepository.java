package com.uva.api.services.users.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.uva.api.services.users.models.Client;

public interface ClientRepository extends JpaRepository<Client, Integer> {
  Optional<Client> findByEmail(String email);
}