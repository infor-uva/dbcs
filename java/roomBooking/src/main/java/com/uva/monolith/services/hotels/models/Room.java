package com.uva.monolith.services.hotels.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.uva.monolith.services.bookings.models.Booking;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "rooms")
// @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
// property = "id")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private int id;

    @ManyToOne
    @JoinColumn(name = "hotel_id", referencedColumnName = "id")
    @JsonIgnore
    private Hotel hotel;
    @Column(name = "room_number", nullable = false)
    private String roomNumber;
    @Column(name = "type", nullable = false)
    private RoomType type;
    @Column(name = "available", nullable = false)
    private boolean available;
    @JsonIgnore
    @OneToMany(mappedBy = "roomId", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Booking> bookings;

    public Room() {
    }

    public Room(int id, Hotel hotelId, String roomNumber, RoomType type, boolean available, List<Booking> bookings) {
        this.id = id;
        this.hotel = hotelId;
        this.roomNumber = roomNumber;
        this.type = type;
        this.available = available;
        this.bookings = bookings;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setHotel(Hotel hotelId) {
        this.hotel = hotelId;
    }

    public Hotel getHotel() {
        return this.hotel;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getRoomNumber() {
        return this.roomNumber;
    }

    public void setType(RoomType type) {
        this.type = type;
    }

    public RoomType getType() {
        return this.type;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isAvailable() {
        return this.available;
    }

    public List<Booking> getBookings() {
        return this.bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }
}
