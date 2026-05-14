package com.hospital;

import java.io.Serializable;
import java.util.ArrayList;

public class Paitent implements Serializable {
  private String id;
  private String name;
  private int age;
  private String phoneNumber;
  private ArrayList<Appointment> visit = new ArrayList<>();

  public Paitent(String id, String name, int age, String phoneNumber) {
    this.id = id;
    this.name = name;
    this.age = age;
    this.phoneNumber = phoneNumber;
  }

  public void addVisit(Appointment appointment) {

    visit.add(appointment);
  }

  public ArrayList<Appointment> getVisits() {
    return visit;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public int getAge() {
    return age;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setAge(int age) {
    this.age = age;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }
}
