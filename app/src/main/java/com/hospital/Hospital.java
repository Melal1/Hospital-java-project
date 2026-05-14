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
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
      oos.writeObject(doctors);
      oos.writeObject(patients);
      System.out.println("All data saved successfully to " + DATA_FILE);
    } catch (IOException e) {
      System.err.println("Error saving data: " + e.getMessage());
    }
  }

  @SuppressWarnings("unchecked")
  public void loadData() {
    File file = new File(DATA_FILE);
    if (!file.exists())
      return;

    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
      doctors = (ArrayList<Doctor>) ois.readObject();
      patients = (ArrayList<Paitent>) ois.readObject();
      System.out.println("Loaded " + doctors.size() + " doctors and " + patients.size() + " patients.");
    } catch (IOException | ClassNotFoundException e) {
      System.err.println("Error loading data: " + e.getMessage());
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

  public void makeAppointment() throws BookingException {
  }

}
