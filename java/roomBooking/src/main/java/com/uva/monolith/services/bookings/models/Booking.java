package com.uva.api.services.bookings.models;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

import com.uva.api.services.hotels.models.Room;
import com.uva.api.services.users.models.Client;

@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private int id;
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne(optional = false, fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private Client userId;
    @JoinColumn(name = "room_id", referencedColumnName = "id")
    @ManyToOne(optional = false, fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private Room roomId;
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    public Booking() {
    }

    public Booking(int id, Client userId, Room roomID, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.userId = userId;
        this.roomId = roomID;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setUserId(Client userId) {
        this.userId = userId;
    }

    public Client getUserId() {
        return this.userId;
    }

    public void setRoomId(Room roomID) {
        this.roomId = roomID;
    }

    public Room getRoomId() {
        return this.roomId;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

}
