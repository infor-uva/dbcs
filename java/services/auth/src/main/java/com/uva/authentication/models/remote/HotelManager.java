package com.uva.authentication.models.remote;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "hotel_manager_user")
public class HotelManager extends User {

  // TODO tener en cuenta que esto hay que tener un control adecuado
  // @JsonIgnore
  // @OneToMany(mappedBy = "userId", fetch = FetchType.EAGER, cascade =
  // CascadeType.ALL)
  // private List<?> hotels;

  public HotelManager() {
    super();
    // hotels = new ArrayList<>();
  }

  public HotelManager(int id, String name, String email, String password) { // , List<?> hotels) {
    super(id, name, email, password, UserRol.HOTEL_ADMIN);
    // setHotels(hotels);
  }

  // public List<?> getHotels() {
  // return this.hotels;
  // }

  // public void setHotels(List<?> hotels) {
  // this.hotels = hotels;
  // }
}
