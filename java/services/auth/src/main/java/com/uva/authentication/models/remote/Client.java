package com.uva.authentication.models.remote;

import java.util.ArrayList;
import java.util.List;

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
  private UserStatus status = UserStatus.NO_BOOKINGS;

  @OneToMany(mappedBy = "userId", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  private List<Booking> bookings = new ArrayList<>();

  public Client() {
    super();
  }

  public Client(int id, String name, String email, String password, UserStatus status, List<Booking> bookings) {
    super(id, name, email, password, UserRol.CLIENT);
    setStatus(status);
    setBookings(bookings);
  }

  public UserStatus getStatus() {
    return this.status;
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
