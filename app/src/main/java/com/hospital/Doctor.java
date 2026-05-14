package com.hospital;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public abstract class Doctor {
  private int id;
  private int age;
  private boolean isAvailable;
  private String name;
  private PriorityQueue<Appointment> appointments;

  public Doctor(int age, int id, String name) {
    this.age = age;
    this.id = id;
    this.name = name;
    this.appointments = new PriorityQueue<>(Comparator.comparing(appointment -> appointment.getStartTime()));
    this.isAvailable = true;
  }

  public int getId() {
    return id;
  }

  public int getAge() {
    return age;
  }

  public boolean isAvailable() {
    return isAvailable;
  }

  public String getName() {
    return this.name;
  }

  public String getFormattedName() {
    return "Dr." + name;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setAge(int age) {
    this.age = age;
  }

  public void setName(String name) {
    this.name = name;
  }

  public PriorityQueue<Appointment> getAppointments() {
    return appointments;
  }

  private boolean hasConflict(Appointment appointment) {
    for (Appointment existing : appointments) {
      if (existing.getStatus() != Status.canceled &&
          appointment.getStartTime().isBefore(existing.getEndTime())
          && appointment.getEndTime().isAfter(existing.getStartTime())) {
        return true;
      }
    }
    return false;
  }

  public boolean addAppointment(Appointment appointment) {

    if (appointment.isEmergency()) {

      // emergency appointment.
      List<Appointment> overlapping = new ArrayList<>();
      for (Appointment existing : appointments) {
        if (existing.getStatus() != Status.canceled &&
            appointment.getStartTime().isBefore(existing.getEndTime())
            && appointment.getEndTime().isAfter(existing.getStartTime())) {
          overlapping.add(existing);
        }
      }

      for (Appointment overlapped : overlapping) {
        appointments.remove(overlapped);
        overlapped.reschedule(1);

        while (hasConflict(overlapped)) {
          overlapped.reschedule(1);
        }

        appointments.add(overlapped);
      }

      appointments.add(appointment);
      return true;
    }

    if (hasConflict(appointment)) {
      return false;
    }

    appointments.add(appointment);
    return true;
  }

  public abstract String getSpeciality();

}
