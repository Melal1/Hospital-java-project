package com.hospital;

public class Operation extends Appointment {

  private Operationtype type;

  public Operation(String startTime, String endTime, Status status, boolean isOperation, boolean isEmergency, Operationtype type, Paitent paitent, String illness) {
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
