package com.uva.authentication.models.remote;

import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "hotels")
public class Hotel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  private int id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "hotel_manager", referencedColumnName = "id")
  private HotelManager hotelManager;

  public Hotel() {
  }

  public Hotel(int id, HotelManager hotelManager) {
    setId(id);
    setHotelManager(hotelManager);
  }

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setHotelManager(HotelManager hotelManager) {
    this.hotelManager = hotelManager;
  }

  public HotelManager getHotelManager() {
    return hotelManager;
  }
}
