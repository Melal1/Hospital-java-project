package com.hospital;

public class SurgeonDoctor extends Doctor {

  public enum Speciality {
    Heart, Orthopedic, Neurological, Plastic
  }

  private Speciality speciality;

  public SurgeonDoctor(int age, int id, String name, Speciality speciality) {
    super(age, id, name);
    this.speciality = speciality;
  }

  @Override
  public String getSpeciality() {
    return speciality.name();
  }

}
