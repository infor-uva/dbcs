package com.uva.authentication.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.uva.authentication.models.Client;
import com.uva.authentication.models.User;

public interface ClientRepository extends JpaRepository<User, Integer> {
  Optional<Client> findByEmail(String email);
}