package com.uva.roomBooking.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

@Entity
@Table(name = "rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private int id;
    @JoinColumn(name = "hotel_id", referencedColumnName = "id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    // @ManyToOne(optional = false, fetch = FetchType.EAGER, cascade =
    // CascadeType.MERGE)
    // @JsonIgnore
    private Hotel hotelId;
    @Column(name = "room_number", nullable = false)
    private int roomNumber;
    @Column(name = "type", nullable = false)
    private Tipo type;
    @Column(name = "available", nullable = false)
    private boolean available;

    public Room() {
    }

    public Room(int id, Hotel hotelId, int roomNumber, Tipo type, boolean available) {
        this.id = id;
        this.hotelId = hotelId;
        this.roomNumber = roomNumber;
        this.type = type;
        this.available = available;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setHotel(Hotel hotelId) {
        this.hotelId = hotelId;
    }

    public Hotel getHotel() {
        return this.hotelId;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getRoomNumber() {
        return this.roomNumber;
    }

    public void setType(Tipo type) {
        this.type = type;
    }

    public Tipo getType() {
        return this.type;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean getAvailable() {
        return this.available;
    }

}
