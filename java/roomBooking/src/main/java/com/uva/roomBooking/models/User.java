package com.uva.roomBooking.models;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {
  // TODO extraer a dos clases hijas, una por cada tipo
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  private int id;

  @Basic(optional = false)
  private String name;

  @Basic(optional = false)
  private String email;

  @Basic(optional = false)
  private String password;

  @Basic(optional = false)
  @Enumerated(EnumType.STRING)
  private UserRol rol = UserRol.CONSUMER;

  @Basic(optional = false)
  @Enumerated(EnumType.STRING)
  private UserStatus status;

  @JsonIgnore
  @OneToMany(mappedBy = "userId", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  private List<Booking> bookings;

  public User() {
  }

  public User(int id, String name, String email, String password, UserRol rol, UserStatus status,
      List<Booking> bookings) {
    setId(id);
    setName(name);
    setEmail(email);
    setRol(rol);
    setStatus(status);
    setBookings(bookings);
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

  public String getEmail() {
    return this.email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public UserRol getRol() {
    return this.rol;
  }

  public void setRol(UserRol rol) {
    this.rol = rol;
  }

  public UserStatus getStatus() {
    if (!getBookings().isEmpty())
      return UserStatus.NO_BOOKINGS;
    boolean activeBookings = getBookings().stream()
        .anyMatch(booking -> !booking.getEndDate().isBefore(LocalDate.now())); // reserva >= ahora
    return activeBookings ? UserStatus.WITH_ACTIVE_BOOKINGS : UserStatus.WITH_INACTIVE_BOOKINGS;
  }

  public void setStatus(UserStatus status) {
    this.status = status;
  }

  public List<Booking> getBookings() {
    return this.bookings;
  }

  public void setBookings(List<Booking> bookings) {
    this.bookings = bookings;
  }
}
