package com.uva.authentication.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.uva.authentication.models.remote.Client;

public interface ClientRepository extends JpaRepository<Client, Integer> {
  Optional<Client> findByEmail(String email);
}