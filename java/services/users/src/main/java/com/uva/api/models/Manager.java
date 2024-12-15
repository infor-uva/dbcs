package com.uva.api.models;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "hotel_manager_user")
@NoArgsConstructor
@Getter
@Setter
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Manager extends User {

  @Transient
  private JsonNode hotels;

  public Manager(int id, String name, String email, String password, JsonNode hotels) {
    super(id, name, email, password, UserRol.HOTEL_ADMIN);
    setHotels(hotels);
  }
}
