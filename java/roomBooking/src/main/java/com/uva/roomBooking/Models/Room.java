package com.uva.roomBooking.Models;

public class Room {
    private int id;
    private Hotel hotelId;
    private int roomNumber;
    private Tipo type;
    private boolean available;

    public Room (int id, Hotel hotelId, int roomNumber, Tipo type, boolean available) {
        this.id = id;
        this.hotelId = hotelId;
        this.roomNumber = roomNumber;
        this.type = type;
        this.available = available;
    }

    public void setId (int id) {
        this.id = id;
    }

    public int getId(){
        return this.id;
    }

    public void setHotel (Hotel hotelId) {
        this.hotelId = hotelId;
    }

    public Hotel getHotel () {
        return this.hotelId;
    }

    public void setRoomNumber (int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getRoomNumber () {
        return this.roomNumber;
    }

    public void setType (Tipo type) {
        this.type = type;
    }

    public Tipo getType () {
        return this.type;
    }

    public void setAvailable (boolean available) {
        this.available = available;
    }

    public boolean getAvailable () {
        return this.available;
    }

}
