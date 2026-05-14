package com.hospital;

public class GeneralDoctor extends Doctor {
  public GeneralDoctor(int age, int id, String name) {
    super(age, id, name);
    addSupportedIllness(Illness.INFLUENZA);
    addSupportedIllness(Illness.ASTHMA);
    addSupportedIllness(Illness.ALLERGY);
    addSupportedIllness(Illness.COVID19);
    addSupportedIllness(Illness.HYPERTENSION);
    addSupportedIllness(Illness.BRONCHITIS);
    addSupportedIllness(Illness.ANEMIA);
  }

  @Override
  public String getSpeciality() {
    return "General";
  }

}
