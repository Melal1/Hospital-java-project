package com.hospital;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class hospital {
    private List<Paitent> patients = new ArrayList<>();
    private List<Doctor> doctors = new ArrayList<>();
    private final String DATA_FILE = "hospital_data.dat";


    public hospital() {
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
        if (!file.exists()) return;

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

    public void makeAppointment(String doctorId, String start, String end) throws BookingException {
        for (Doctor d : doctors) {
            if (d.Id.equals(doctorId)) {
                Check_UP checkup = new Check_UP(start, end, Status.Scheduled, false);
                if (d.addCheakUp(checkup)) {
                    saveData();
                    return;
                } else {
                    throw new BookingException("Time slot is already taken.");
                }
            }
        }
        throw new BookingException("Doctor not found.");
    }

    public void makeOperation(String doctorId, String start, String end, Operationtype type) throws BookingException {
        for (Doctor d : doctors) {
            if (d instanceof SurgeonDoctor && d.Id.equals(doctorId)) {
                Operation op = new Operation(start, end, Status.Scheduled, true, type);
                SurgeonDoctor surgeon = (SurgeonDoctor) d;
                if (surgeon.addOperation(op)) {
                    saveData();
                    return;
                } else {
                    throw new BookingException("Surgeon is busy or time slot taken.");
                }
            }
        }
        throw new BookingException("Surgeon not found.");
    }
}