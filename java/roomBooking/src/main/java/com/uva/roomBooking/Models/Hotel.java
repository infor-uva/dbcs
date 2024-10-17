package com.uva.roomBooking.Models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "hotels")
public class Hotel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  private int id;

  @Basic(optional = false)
  private String name;

  @JoinColumn(name = "address_id", referencedColumnName = "id")
  @OneToOne(optional = false, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private Address address;

  @OneToMany(mappedBy = "hotelId", fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
  private List<Room> rooms;

  public Hotel() {
  }

  public Hotel(int id, String name, Address address, List<Room> rooms) {
    setId(id);
    setName(name);
    setAddress(address);
    setRooms(rooms);
  }

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Address getAddress() {
    return this.address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public List<Room> getRooms() {
    return this.rooms;
  }

  public void setRooms(List<Room> rooms) {
    this.rooms = rooms;
  }

}
