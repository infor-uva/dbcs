package com.uva.roomBooking.Models;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "id")
  private int id;

  @Basic(optional = false)
  @Column(name = "name")
  private String name;

  @Basic(optional = false)
  @Column(name = "email")
  private String email;

  @Basic(optional = false)
  @Column(name = "status")
  @Enumerated(EnumType.STRING)
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
