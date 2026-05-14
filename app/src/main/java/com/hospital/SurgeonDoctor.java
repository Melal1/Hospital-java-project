package com.hospital;

public class SurgeonDoctor extends Doctor {

  public enum Speciality {
    Heart, Orthopedic, Neurological, Plastic
  }

  private Speciality speciality;

  public SurgeonDoctor(int age, int id, String name, Speciality speciality) {
    super(age, id, name);
    this.speciality = speciality;
    switch (speciality) {
      case Heart:
        addSupportedIllness(Illness.HEART_ATTACK);
        break;
      case Orthopedic:
        addSupportedIllness(Illness.FRACTURE);
        addSupportedIllness(Illness.ARTHRITIS);
        break;
      case Neurological:
        addSupportedIllness(Illness.STROKE);
        addSupportedIllness(Illness.ALZHEIMERS);
        break;
      case Plastic:
        addSupportedIllness(Illness.CANCER);
        break;
    }
  }

  @Override
  public String getSpeciality() {
    return speciality.name();
  }

}
