package com.hospital;

import java.io.Serializable;
import java.time.LocalTime;

enum Status {
  Completed, Canceled, Scheduled, On_Going
}

abstract public class Appointment implements Serializable {
  private static final long serialVersionUID = 1L;


  private LocalTime AppointmentStart;
  private LocalTime AppointmentEnd;
  private Status status;
  private String TypeDisease;
  private boolean IsOperation;

  public Appointment(String AppointmentStartt, String AppointmentEndd, Status status, boolean isOperation) {
    this.AppointmentEnd = LocalTime.parse(AppointmentEndd);
    this.AppointmentStart = LocalTime.parse(AppointmentStartt);
    this.status = status;
    this.IsOperation = isOperation;
  }

  // The opertation start with illnese type with paitent
  abstract public String Treat();

  void CanceledAppointment() {

    status = Status.Canceled;

  }

  void FinashAppointment() {

    status = Status.Completed;
  }

  public LocalTime getAppointmentStart() {
    return AppointmentStart;
  }

  public LocalTime getAppointmentEnd() {
    return AppointmentEnd;
  }

  public Status getStatus() {
    return status;
  }

  public String getTypeDisease() {
    return TypeDisease;
  }

  public boolean isOperation() {
    return IsOperation;
  }
}
