package com.uva.roomBooking.Models;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Address")
public class Address {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "ID")
  private int id;

  @Basic(optional = false)
  @Column(name = "STREET_KIND")
  private String streetKind;

  @Basic(optional = false)
  @Column(name = "STREET_NAME")
  private String streetName;

  @Basic(optional = false)
  @Column(name = "NUMBER")
  private int number;

  @Basic(optional = false)
  @Column(name = "POST_CODE")
  private String postCode;

  @Basic(optional = true)
  @Column(name = "OTHER_INFO")
  private String otherInfo;

  public Address() {
  }

  public Address(String streetKind, String streetName, int number, String postCode, String otherInfo) {
    setStreetKind(streetKind);
    setStreetName(streetName);
    setNumber(number);
    setPostCode(postCode);
    setOtherInfo(otherInfo);
  }

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getStreetKind() {
    return this.streetKind;
  }

  public void setStreetKind(String streetKind) {
    this.streetKind = streetKind;
  }

  public String getStreetName() {
    return this.streetName;
  }

  public void setStreetName(String streetName) {
    this.streetName = streetName;
  }

  public int getNumber() {
    return this.number;
  }

  public void setNumber(int number) {
    this.number = number;
  }

  public String getPostCode() {
    return this.postCode;
  }

  public void setPostCode(String postCode) {
    this.postCode = postCode;
  }

  public String getOtherInfo() {
    return this.otherInfo;
  }

  public void setOtherInfo(String otherInfo) {
    this.otherInfo = otherInfo;
  }

}
