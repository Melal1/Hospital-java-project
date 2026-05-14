package com.hospital;

public enum Operationtype {
  Cardiacsurgery(SurgeonDoctor.Speciality.Heart),
  ValveReplacement(SurgeonDoctor.Speciality.Heart),
  Orthopedicsurgery(SurgeonDoctor.Speciality.Orthopedic),
  KneeReplacement(SurgeonDoctor.Speciality.Orthopedic),
  Neurosurgery(SurgeonDoctor.Speciality.Neurological),
  BrainSurgery(SurgeonDoctor.Speciality.Neurological),
  Rhinoplasty(SurgeonDoctor.Speciality.Plastic);

  private final SurgeonDoctor.Speciality requiredSpeciality;

  Operationtype(SurgeonDoctor.Speciality requiredSpeciality) {
    this.requiredSpeciality = requiredSpeciality;
  }

  public SurgeonDoctor.Speciality getRequiredSpeciality() {
    return requiredSpeciality;
  }
}
