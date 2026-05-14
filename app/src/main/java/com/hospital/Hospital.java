package com.hospital;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Hospital {
  private List<Paitent> patients = new ArrayList<>();
  private List<Doctor> doctors = new ArrayList<>();
  private final String DATA_FILE = "hospital_data.dat";

  public Hospital() {
    loadData();
  }

  public void saveData() {
    File file = new File(DATA_FILE);
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
      oos.writeObject(doctors);
      oos.writeObject(patients);
      System.out.println("All data saved successfully to " + file.getAbsolutePath());
    } catch (IOException e) {
      System.err.println("Error saving data: " + e.getMessage());
      e.printStackTrace();
    }
  }

  @SuppressWarnings("unchecked")
  public void loadData() {
    File file = new File(DATA_FILE);
    if (!file.exists()) {
      System.out.println("No data file found at " + file.getAbsolutePath());
      return;
    }

    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
      doctors = (ArrayList<Doctor>) ois.readObject();
      patients = (ArrayList<Paitent>) ois.readObject();
      System.out.println("Loaded " + doctors.size() + " doctors and " + patients.size() + " patients from " + file.getAbsolutePath());
    } catch (IOException | ClassNotFoundException e) {
      System.err.println("Error loading data from " + file.getAbsolutePath() + ": " + e.getMessage());
      e.printStackTrace();
    }
  }

  public void addDoctor(Doctor newDoctor) {
    doctors.add(newDoctor);
    saveData();
  }

  public void addPatient(Paitent newPatient) {
    patients.add(newPatient);
    saveData();
  }

  public List<Doctor> getDoctors() {
    return this.doctors;
  }

  public List<Paitent> getPatients() {
    return this.patients;
  }

  public void makeAppointment(Doctor doctor, Paitent patient, Appointment appointment) throws BookingException {
    // Patient Overlap Check
    for (Appointment existing : patient.getVisits()) {
      if (existing.getStatus() != Status.canceled &&
          appointment.getStartTime().isBefore(existing.getEndTime()) &&
          appointment.getEndTime().isAfter(existing.getStartTime())) {
        throw new BookingException("Patient already has an appointment during this time: " +
            existing.getStartTime().toString() + " to " + existing.getEndTime().toString());
      }
    }

    // Doctor Conflict Check
    if (doctor.addAppointment(appointment)) {
      patient.addVisit(appointment);
      saveData();
    } else {
      throw new BookingException("Doctor has a scheduling conflict.");
    }
  }

}
