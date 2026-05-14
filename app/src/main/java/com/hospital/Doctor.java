package com.hospital;

import java.io.*;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.io.Serializable;

 class AppointmentConflictException extends Exception {
  public AppointmentConflictException(String message) {
    super(message);
  }
}

 class DoctorNotFoundException extends Exception {
  public DoctorNotFoundException(String message) {
    super(message);
  }
}
enum DoctorStatus {

  Available, Non_Available

}


 public abstract class Doctor implements Serializable {
   private static final long serialVersionUID = 1L;

  String Id;
  String Name;
  int Age;
  PriorityQueue<Appointment> appointments;

  DoctorStatus doctorStatus;

  public Doctor(int age, DoctorStatus doctorStatus, String id, String name) {
    Age = age;
    this.doctorStatus = doctorStatus;
    Id = id;
    Name = name;

      this.appointments = new PriorityQueue<>(
              (Comparator<Appointment> & Serializable) (a, b) -> a.getAppointmentStart().compareTo(b.getAppointmentStart())
      );
  }

  public boolean AddAppointment(Appointment Newappointment) {

    for (Appointment existing : appointments) {
      if (Newappointment.getAppointmentStart().isBefore(existing.getAppointmentEnd())
          && Newappointment.getAppointmentEnd().isAfter(existing.getAppointmentStart())) {

        return false;
      }
    }
    appointments.add(Newappointment);
    return true;
  }

  boolean IsAvaulable() {

    return (doctorStatus == DoctorStatus.Available);

  }

  public abstract boolean addCheakUp(Check_UP Checkup);

}
