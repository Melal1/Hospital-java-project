package com.hospital;

public class CheckUp extends Appointment {
  public CheckUp(String startTime, String endTime, boolean isEmergency, Paitent paitent, String illness) {
    super(startTime, endTime, false, isEmergency, paitent, illness);
  }

  @Override
  public String Treat() {
    return "Checking up on " + super.getPaitent().getName() + " for illness " + getIllness();
  }
}
