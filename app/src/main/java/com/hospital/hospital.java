package com.hospital;

import java.util.ArrayList;
import java.util.List;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class hospital {

    private List<Paitent> patients = new ArrayList<>();
    private List<Doctor> doctors = new ArrayList<>();

    public void addPatient(Paitent newPatient) {
        patients.add(newPatient);
        savePatientToFile(newPatient);
        System.out.println("Patient added and saved successfully!");
    }

    private void savePatientToFile(Paitent p) {

        try (FileWriter fw = new FileWriter("patients.txt", true);
             PrintWriter out = new PrintWriter(fw)) {

            out.println(p.getId() + "," + p.getName() + "," + p.getAge() + "," + p.getPhoneNumber());

        } catch (IOException e) {
            System.err.println("Error saving to file: " + e.getMessage());
        }
    }


    public void showAllPatients()
    {
        for (Paitent p : patients) {
            System.out.println("ID: " + p.getId() + " | Name: " + p.getName());
        }
    }



    public List<Paitent> loadPatientsFromFile() {
        List<Paitent> loadedPatients = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("patients.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {

                String[] data = line.split(",");


                if (data.length == 4) {
                    String id = data[0];
                    String name = data[1];
                    int age = Integer.parseInt(data[2]);
                    String phone = data[3];


                    Paitent p = new Paitent(id, name, age, phone);
                    loadedPatients.add(p);
                }
            }
        } catch (IOException e) {
            System.err.println("Patient file not found or the file is empty");        }


        this.patients = loadedPatients;
        return loadedPatients;
    }


    public void addDoctor(Doctor newDoctor) {
        doctors.add(newDoctor);
        saveDoctorToFile(newDoctor);
    }

    private void saveDoctorToFile(Doctor d) {

        try (FileWriter fw = new FileWriter("doctors.txt", true);
             PrintWriter out = new PrintWriter(fw)) {

            String type = d.getClass().getSimpleName();
            out.println(type + "," + d.Id + "," + d.Name + "," + d.Age + "," + d.doctorStatus);
        } catch (IOException e) {
            System.err.println("Erorr in saving the doctor" + e.getMessage());
        }
    }

    public List<Doctor> getDoctors() {
        return this.doctors;
    }




        public void loadDoctorsFromFile() {
            doctors.clear();
            try (BufferedReader br = new BufferedReader(new FileReader("doctors.txt"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] data = line.split(",");
                    String type = data[0];
                    String id = data[1];
                    String name = data[2];
                    int age = Integer.parseInt(data[3]);
                    DoctorStatus status = DoctorStatus.valueOf(data[4]);

                    Doctor d = null;
                    if (type.equals("GeneralDoctor")) d = new GeneralDoctor(age, status, id, name, null);
                    else if (type.equals("SurgeonDoctor")) d = new SurgeonDoctor(age, status, id, name);
                    else if (type.equals("SpecialistDoctor")) d = new SpecialistDoctor(age, status, id, name, null);
                    if (d != null) {
                        d.loadAppointmentsFromFile();
                        doctors.add(d);
                    }


                }
            } catch (IOException e) {
            System.err.println("No doctors file currently exists.");
        }
    }


     public void makeAppointment(String doctorId, String start, String end) throws BookingException {
        for (Doctor d : doctors) {
            if (d.Id.equals(doctorId)) {
                if (!d.IsAvaulable()) {
                    throw new BookingException("Booking failed: Doctor " + d.Name + " is currently unavailable.");
                }

                Check_UP checkup = new Check_UP(start, end, Status.Scheduled, false);
                if (d.addCheakUp(checkup)) {
                    d.saveAppointmentsToFile();
                    System.out.println("Appointment successfully scheduled with Dr. " + d.Name);
                    return;
                } else {
                    throw new BookingException("Booking failed: Time slot " + start + "-" + end + " is already taken.");
                }
            }
        }
        throw new BookingException("Booking failed: Doctor with ID " + doctorId + " not found.");
    }
//    public boolean makeAppointment (String doctorId, String start, String end) {
//        for (Doctor d : doctors) {
//            if (d.Id.equals(doctorId) && d.IsAvaulable()) { // هل isavailable  هي ان الدكتور بعطلة ام مداوم
//                Check_UP checkup = new Check_UP(start, end, Status.Scheduled, false);
//                if (d.addCheakUp(checkup)) {
//                    d.saveAppointmentsToFile();
//                    System.out.println("Appointment successfully scheduled with Dr. " + d.Name);                    return true;
//                }
//            }
//        }
//        System.out.println("Booking failed: Doctor not found, unavailable, or time slot already taken.");        return false;
//    }


    public boolean makeOperation(String doctorId, String start, String end, Operationtype type) {
        for (Doctor d : doctors) {
            if (d instanceof SurgeonDoctor && d.Id.equals(doctorId) && d.IsAvaulable()) {// هل isavailable  هي ان الدكتور بعطلة ام مداوم
                Operation op = new Operation(start, end, Status.Scheduled, true, type);
                SurgeonDoctor surgeon = (SurgeonDoctor) d;
                if (surgeon.addOperation(op)) {
                    d.saveAppointmentsToFile();
                    System.out.println("Operation successfully scheduled with Surgeon: " + d.Name);                    return true;
                }
            }
        }
        System.out.println("Booking failed: Surgeon unavailable or cannot perform this operation at this time.");        return false;
    }
}