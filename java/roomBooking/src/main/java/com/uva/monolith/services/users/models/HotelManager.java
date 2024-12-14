package com.uva.monolith.services.users.models;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.uva.monolith.services.hotels.models.Hotel;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "hotel_manager_user")
public class HotelManager extends User {

  @JsonIgnore
  @OneToMany(mappedBy = "hotelManager", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  private List<Hotel> hotels;

  public HotelManager() {
    super();
    hotels = new ArrayList<>();
  }

  public HotelManager(int id, String name, String email, String password, List<Hotel> hotels) {
    super(id, name, email, password, UserRol.HOTEL_ADMIN);
    setHotels(hotels);
  }

  public List<Hotel> getHotels() {
    return this.hotels;
  }

  public void setHotels(List<Hotel> hotels) {
    this.hotels = hotels;
  }
}
