package com.uva.authentication.models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_client")
public class Client extends User {

  @Basic(optional = false)
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private UserStatus status;

  // @JsonIgnore
  // @OneToMany(mappedBy = "userId", fetch = FetchType.EAGER, cascade =
  // CascadeType.ALL)
  // private List<?> bookings;

  public Client() {
    super();
    // bookings = new ArrayList<>();
    status = UserStatus.NO_BOOKINGS;
  }

  public Client(int id, String name, String email, String password, UserStatus status) {
    // , List<?> bookings) {
    super(id, name, email, password, UserRol.CLIENT);
    setStatus(status);
    // setBookings(bookings);
  }

  public UserStatus getStatus() {
    return this.status;
  }

  public void setStatus(UserStatus status) {
    this.status = status;
  }

  // public List<?> getBookings() {
  // return this.bookings;
  // }

  // public void setBookings(List<?> bookings) {
  // this.bookings = bookings;
  // }
}
