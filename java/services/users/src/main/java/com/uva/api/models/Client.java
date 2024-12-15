package com.uva.api.models;

import java.time.LocalDate;
import java.util.List;

import com.uva.api.models.remote.Booking;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "user_client")
@NoArgsConstructor
@Getter
@Setter
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Client extends User {

  @Basic(optional = false)
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ClientStatus status = ClientStatus.NO_BOOKINGS;
  @Transient
  private List<Booking> bookings;

  public Client(int id, String name, String email, String password, ClientStatus status,
      List<Booking> bookings) {
    super(id, name, email, password, UserRol.CLIENT);
    setStatus(status);
    setBookings(bookings);
  }

  public ClientStatus getStatus() {
    if (getBookings() == null || getBookings().isEmpty())
      return ClientStatus.NO_BOOKINGS;
    boolean activeBookings = getBookings().stream()
        .anyMatch(booking -> !booking.getEndDate().isBefore(LocalDate.now())); // reserva >= ahora
    return activeBookings ? ClientStatus.WITH_ACTIVE_BOOKINGS : ClientStatus.WITH_INACTIVE_BOOKINGS;
  }

  public void setStatus(ClientStatus status) {
    this.status = status;
  }
}
