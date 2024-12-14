package com.uva.api.services.users.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.uva.api.services.bookings.models.Booking;

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

  @JsonIgnore
  @OneToMany(mappedBy = "userId", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  private List<Booking> bookings;

  public Client() {
    super();
    bookings = new ArrayList<>();
    status = UserStatus.NO_BOOKINGS;
  }

  public Client(int id, String name, String email, String password, UserStatus status,
      List<Booking> bookings) {
    super(id, name, email, password, UserRol.CLIENT);
    setStatus(status);
    setBookings(bookings);
  }

  public UserStatus getStatus() {
    if (getBookings() == null || getBookings().isEmpty())
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
