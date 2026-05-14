package com.hospital;

public class Operation extends Appointment {
  private static final long serialVersionUID = 1L;

  private Operationtype type;

  public Operation(String startTime, String endTime, Status status, boolean isOperation, boolean isEmergency,
      Operationtype type, Paitent paitent, Illness illness) {
    super(startTime, endTime, isOperation, isEmergency, paitent, illness);
    this.type = type;
  }

  public final Operationtype getType() {
    return this.type;
  }

  @Override
  public String Treat() {
    return "Doing operation for patient " + super.getPaitent();
  }
}
