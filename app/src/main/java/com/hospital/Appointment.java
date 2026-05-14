package com.hospital;

import java.io.Serializable;

import java.time.LocalDateTime;

abstract public class Appointment implements Serializable {
  private static final long serialVersionUID = 1L;

  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private Status status;
  private Illness illness;
  private boolean isOperation;
  private boolean isEmergency;
  private Paitent paitent;

  public Appointment(String startTime, String endTime, boolean isOperation, boolean isEmergency, Paitent paitent,
      Illness illness) {
    this.endTime = LocalDateTime.parse(endTime);
    this.startTime = LocalDateTime.parse(startTime);
    this.status = Status.scheduled;
    this.isOperation = isOperation;
    this.isEmergency = isEmergency;
    this.paitent = paitent;
    this.illness = illness;
  }

  abstract public String Treat();

  public void cancel() {

    status = Status.canceled;

  }

  public void finish() {

    status = Status.completed;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public void reschedule(long days) {
    this.startTime = this.startTime.plusDays(days);
    this.endTime = this.endTime.plusDays(days);
    this.status = Status.rescheduled;
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
