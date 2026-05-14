package com.hospital;

public class SpecialistDoctor extends Doctor {
  public enum Speciality {
    Cardiology, Orthopedics, Dermatology
  }

  private Speciality speciality;

  public SpecialistDoctor(int age, int id, String name, Speciality speciality) {
    super(age, id, name);
    this.speciality = speciality;
    switch (speciality) {
      case Cardiology:
        addSupportedIllness(Illness.HEART_ATTACK);
        addSupportedIllness(Illness.HYPERTENSION);
        addSupportedIllness(Illness.ANEMIA);
        break;
      case Orthopedics:
        addSupportedIllness(Illness.ARTHRITIS);
        addSupportedIllness(Illness.OSTEOPOROSIS);
        addSupportedIllness(Illness.FRACTURE);
        break;
      case Dermatology:
        addSupportedIllness(Illness.ALLERGY);
        break;
    }
  }

  @Override
  public String getSpeciality() {
    return speciality.name();
  }

}
