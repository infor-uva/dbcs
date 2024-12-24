package com.uva.api.hotels.models;

import java.util.List;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "hotels")
// @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
// property = "id")

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

  @OneToMany(mappedBy = "hotel", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  private List<Room> rooms;

  @Column(name = "manager_id")
  private int managerId;

  public void setRooms(List<Room> rooms) {
    this.rooms = rooms;
    rooms.forEach(r -> r.setHotel(this));
  }
}
