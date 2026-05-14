package com.hospital;

public class CheckUp extends Appointment {
  private static final long serialVersionUID = 1L;
  public CheckUp(String startTime, String endTime, boolean isEmergency, Paitent paitent, Illness illness) {
    super(startTime, endTime, false, isEmergency, paitent, illness);
  }

  @Override
  public String Treat() {
    return "Checking up on " + super.getPaitent().getName() + " for illness " + getIllness();
  }
}
