package com.hospital;

import java.io.*;
import java.util.Comparator;
import java.util.PriorityQueue;

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

public abstract class Doctor {
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
    this.appointments = new PriorityQueue<>(Comparator.comparing(appointment -> appointment.getAppointmentStart()));
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
//يجب حفظ العمليات والمواعيد ك operations
  public void saveAppointmentsToFile() {
    String fileName = "appointments_dr_" + this.Id + ".txt";
    try (FileWriter fw = new FileWriter(fileName, false);
         PrintWriter out = new PrintWriter(fw)) {

      for (Appointment app : appointments) {
        if (app instanceof Operation) {
          Operation op = (Operation) app;

          out.println("Operation," + app.getAppointmentStart() + "," +
                  app.getAppointmentEnd() + "," + app.getStatus() + "," + op.getType());
        } else {

          out.println("CheckUp," + app.getAppointmentStart() + "," +
                  app.getAppointmentEnd() + "," + app.getStatus() + ",NONE");
        }
      }
    } catch (IOException e) {
      System.err.println("Error saving appointments for Dr. " + Name);
    }
  }

  public void loadAppointmentsFromFile() {
    String fileName = "appointments_dr_" + this.Id + ".txt";
    File file = new File(fileName);

    if (!file.exists()) return;

    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
      String line;
      while ((line = br.readLine()) != null) {
        String[] data = line.split(",");

        if (data.length == 5) {
          String type = data[0];
          String start = data[1];
          String end = data[2];
          Status status = Status.valueOf(data[3]);
          String specificType = data[4];

          Appointment app;
          if (type.equals("Operation")) {

            Operationtype opType = Operationtype.valueOf(specificType);
            app = new Operation(start, end, status, true, opType);
          } else {
            app = new Check_UP(start, end, status, false);
          }

          this.appointments.add(app);
        }
      }
    } catch (IOException e) {
      System.err.println("خطأ أثناء تحميل مواعيد الدكتور " + Name);
    }
  }
}
