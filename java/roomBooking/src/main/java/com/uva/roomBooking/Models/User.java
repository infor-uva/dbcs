package com.uva.roomBooking.Models;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "ID")
  private int id;

  @Basic(optional = false)
  @Column(name = "NAME")
  private String name;

  @Basic(optional = false)
  @Column(name = "EMAIL")
  private String email;

  @Basic(optional = false)
  @Column(name = "STATUS")
  private UseStatus status;

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public UseStatus getStatus() {
    return this.status;
  }

  public void setStatus(UseStatus status) {
    this.status = status;
  }

}
