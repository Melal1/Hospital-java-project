package com.hospital;

public class SpecialistDoctor extends Doctor {
  public enum Speciality {
    Cardiology, Orthopedics, Dermatology
  }

  private Speciality speciality;

  public SpecialistDoctor(int age, int id, String name, Speciality speciality) {
    super(age, id, name);
    this.speciality = speciality;
  }

  @Override
  public String getSpeciality() {
    return speciality.name();
  }

}
