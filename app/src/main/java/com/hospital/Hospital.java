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
  private String dataFile = "hospital_data.dat";

  public Hospital() {
    loadData();
  }

  public Hospital(boolean isNormalMode) {
    if (isNormalMode) {
      dataFile = "hospital_data.dat";
      loadData();
    } else {
      dataFile = "hospital_data_tutorial.dat";
    }
  }

  public void generateTutorialData() {
    addDoctor(new GeneralDoctor(45, 1, "Mahmoud"));
    addDoctor(new SpecialistDoctor(40, 2, "Samir", SpecialistDoctor.Speciality.Cardiology));
    addDoctor(new SpecialistDoctor(50, 3, "Khaled", SpecialistDoctor.Speciality.Orthopedics));
    addDoctor(new SpecialistDoctor(35, 4, "Nadia", SpecialistDoctor.Speciality.Dermatology));

    addDoctor(new SurgeonDoctor(55, 5, "Tarek", SurgeonDoctor.Speciality.Heart));
    addDoctor(new SurgeonDoctor(48, 6, "Layla", SurgeonDoctor.Speciality.Orthopedic));
    addDoctor(new SurgeonDoctor(52, 7, "Mostafa", SurgeonDoctor.Speciality.Neurological));
    addDoctor(new SurgeonDoctor(42, 8, "Yasmine", SurgeonDoctor.Speciality.Plastic));

    addPatient(new Paitent("101", "Ahmed", 30, "0501111111"));
    addPatient(new Paitent("102", "Fatima", 25, "0502222222"));
    addPatient(new Paitent("103", "Mohamed", 40, "0503333333"));
    addPatient(new Paitent("104", "Aisha", 35, "0504444444"));
    addPatient(new Paitent("105", "Omar", 50, "0505555555"));
  }

  public void saveData() {
    File file = new File(dataFile);
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
    File file = new File(dataFile);
    if (!file.exists()) {
      System.out.println("No data file found at " + file.getAbsolutePath());
      return;
    }

    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
      doctors = (ArrayList<Doctor>) ois.readObject();
      patients = (ArrayList<Paitent>) ois.readObject();
      System.out.println(
          "Loaded " + doctors.size() + " doctors and " + patients.size() + " patients from " + file.getAbsolutePath());
    } catch (java.io.EOFException e) {
      System.err.println("Data file is empty or corrupted (EOF), Starting with empty data.");
      doctors = new ArrayList<>();
      patients = new ArrayList<>();
    } catch (java.io.InvalidClassException e) {
      System.err
          .println("Data format changed (InvalidClassException,, Old data is incompatible, Starting with empty data.");
      doctors = new ArrayList<>();
      patients = new ArrayList<>();
    } catch (IOException | ClassNotFoundException e) {
      System.err.println("Error loading data from " + file.getAbsolutePath() + ": " + e.getMessage());
      doctors = new ArrayList<>();
      patients = new ArrayList<>();
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

  public List<Message> getMessages() {
    List<Message> allMessages = new ArrayList<>();
    for (Paitent p : patients) {
      allMessages.addAll(p.getMessages());
    }
    return allMessages;
  }

  public void makeAppointment(Doctor doctor, Paitent patient, Appointment appointment) throws BookingException {
    for (Appointment existing : patient.getVisits()) {
      if (existing.getStatus() != Status.canceled &&
          appointment.getStartTime().isBefore(existing.getEndTime()) &&
          appointment.getEndTime().isAfter(existing.getStartTime())) {
        throw new BookingException("Patient already has an appointment during this time: " +
            existing.getStartTime().toString() + " to " + existing.getEndTime().toString());
      }
    }

    List<Appointment> affected = new ArrayList<>();
    if (appointment.isEmergency()) {
      for (Appointment existing : doctor.getAppointments()) {
        if (existing.getStatus() != Status.canceled &&
            appointment.getStartTime().isBefore(existing.getEndTime()) &&
            appointment.getEndTime().isAfter(existing.getStartTime())) {
          affected.add(existing);
        }
      }
    }

    if (doctor.addAppointment(appointment)) {
      patient.addVisit(appointment);

      for (Appointment appt : affected) {
        appt.getPaitent()
            .sendMessage("Your appointment has been rescheduled to " + appt.getStartTime() + " due to an emergency.");
      }

      saveData();
    } else {
      throw new BookingException("Doctor has a scheduling conflict.");
    }
  }

}
