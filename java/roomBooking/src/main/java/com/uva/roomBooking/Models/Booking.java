package com.uva.roomBooking.Models;

import java.util.Date;

public class Booking {
    private int id;
    private User userId;
    private Room roomID;
    private Date startDate;
    private Date endDate;


    public Booking (int id, User userId, Room roomID, Date startDate, Date endDate) {
        this.id = id;
        this.userId = userId;
        this.roomID = roomID;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void setId (int id) {
        this.id = id;
    }

    public int getId () {
        return this.id;
    }

    public void setUser (User userId) {
        this.userId = userId;
    }

    public User getUser () {
        return this.userId;
    }

    public void setRoom (Room roomID) {
        this.roomID = roomID;
    }

    public Room getRoom () {
        return this.roomID;
    }

    public void setStartDate (Date startDate) {
        this.startDate = startDate;
    }

    public Date getStartDate () {
        return this.startDate;
    }

    public void setEndDate (Date endDate) {
        this.endDate = endDate;
    }

    public Date getEndDate () {
        return this.endDate;
    }


}

