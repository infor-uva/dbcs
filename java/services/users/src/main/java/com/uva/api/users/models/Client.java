package com.uva.api.users.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
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

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ClientStatus status = ClientStatus.NO_BOOKINGS;

  public Client(int id, String name, String email, String password, ClientStatus status) {
    super(id, name, email, password, UserRol.CLIENT);
    setStatus(status);
  }

  // public ClientStatus getStatus() {
  // if (getBookings() == null || getBookings().isEmpty())
  // return ClientStatus.NO_BOOKINGS;
  // boolean activeBookings = getBookings().stream()
  // .anyMatch(booking -> !booking.getEndDate().isBefore(LocalDate.now())); //
  // reserva >= ahora
  // return activeBookings ? ClientStatus.WITH_ACTIVE_BOOKINGS :
  // ClientStatus.WITH_INACTIVE_BOOKINGS;
  // }
}
