package com.uva.monolith.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "addresses")
public class Address {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  private int id;

  @Basic(optional = false)
  @Column(name = "street_kind")
  private String streetKind;

  @Basic(optional = false)
  @Column(name = "street_name")
  private String streetName;

  @Basic(optional = false)
  private int number;

  @Basic(optional = false)
  @Column(name = "post_code")
  private String postCode;

  @Basic(optional = true)
  @Column(name = "other_info")
  private String otherInfo;

  @JsonIgnore
  @OneToOne(mappedBy = "address", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  private Hotel hotel;

  public Address() {
  }

  public Address(String streetKind, String streetName, int number, String postCode, String otherInfo, Hotel hotel) {
    setStreetKind(streetKind);
    setStreetName(streetName);
    setNumber(number);
    setPostCode(postCode);
    setOtherInfo(otherInfo);
    setHotel(hotel);
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

  public Hotel getHotel() {
    return this.hotel;
  }

  public void setHotel(Hotel hotel) {
    this.hotel = hotel;
  }
}
