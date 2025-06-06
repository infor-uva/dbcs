package com.uva.api.bookings.models;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Table(name = "bookings")
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "manager_id", nullable = false)
    private int managerId;

    @Column(name = "hotel_id", nullable = false)
    private int hotelId;

    @Column(name = "room_id", nullable = false)
    private int roomId;

    @Column(name = "start", nullable = false)
    private LocalDate start;

    @Column(name = "end", nullable = false)
    private LocalDate end;

}
