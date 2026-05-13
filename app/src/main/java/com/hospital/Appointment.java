package com.hospital;

import java.time.LocalDateTime;

enum Status {
  completed, canceled, scheduled, onGoing
}

enum Illness {
    DIABETES, HYPERTENSION, INFLUENZA, ASTHMA, ARTHRITIS, ALZHEIMERS, PNEUMONIA, OSTEOPOROSIS, TUBERCULOSIS, CHOLERA, MALARIA, ANEMIA, BRONCHITIS, HEPATITIS;
}

abstract public class Appointment {

  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private Status status;
  private Illness illness;
  private boolean isOperation;
  private boolean isEmergency;
  private Paitent paitent;

  public Appointment(String startTime, String endTime, boolean isOperation, boolean isEmergency) {
    this.endTime = LocalDateTime.parse(endTime);
    this.startTime = LocalDateTime.parse(startTime);
    this.status = Status.scheduled;
    this.isOperation = isOperation;
    this.isEmergency = isEmergency;
  }

  abstract public String Treat();

  void cancel() {

    status = Status.canceled;

  }

  void finish() {

    status = Status.completed;
  }

  public void reschedule(long days) {
    this.startTime = this.startTime.plusDays(days);
    this.endTime = this.endTime.plusDays(days);
  }

  public LocalDateTime getStartTime() {
    return startTime;
  }

  public LocalDateTime getEndTime() {
    return endTime;
  }

  public void setStartTime(LocalDateTime startTime) {
    this.startTime = startTime;
  }

  public void setEndTime(LocalDateTime endTime) {
    this.endTime = endTime;
  }

  public Status getStatus() {
    return status;
  }
  
  public Illness getIllness() {
      return illness;
  }
    
  public final Paitent getPaitent() {
    return paitent;
  }

  public boolean isOperation() {
    return isOperation;
  }

  public boolean isEmergency() {
    return isEmergency;
  }
}
