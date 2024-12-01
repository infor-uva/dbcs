package com.uva.authentication.models.remote;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "hotel_manager_user")
public class HotelManager extends User {

  @OneToMany(mappedBy = "hotelManager", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  private List<Hotel> hotels = new ArrayList<>();

  public HotelManager() {
    super();
  }

  public HotelManager(int id, String name, String email, String password, List<Hotel> hotels) {
    super(id, name, email, password, UserRol.HOTEL_ADMIN);
    setHotels(hotels);
  }

  public void setHotels(List<Hotel> hotels) {
    this.hotels = hotels;
  }

}
