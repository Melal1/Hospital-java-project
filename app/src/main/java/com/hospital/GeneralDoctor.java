package com.hospital;

public class GeneralDoctor extends Doctor {
  public GeneralDoctor(int age, int id, String name) {
    super(age, id, name);
  }

  @Override
  public String getSpeciality() {
    return "General";
  }

}
