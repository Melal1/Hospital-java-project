package com.hospital;

public class App {

  public static void main(String[] args) {

    hospital myHospital = new hospital();
      Paitent p = new Paitent("P101","Ali",19 ,"0932232323");
      myHospital.addPatient(p);

      myHospital.addDoctor(new GeneralDoctor(33,DoctorStatus.Available,"D102","Dr. Mohammmad",p));

      // التأكد إذا كان هناك دكاترة تم تحميلهم
      if (myHospital.getDoctors().isEmpty()) {
        System.out.println("No data found in the file. Add some data first.");

        // كود تجريبي لإضافة بيانات إذا كان الملف فارغاً لأول مرة
        try {
       myHospital.addDoctor(new SurgeonDoctor(45, DoctorStatus.Available, "D101", "Dr. Ahamd"));
          myHospital.makeOperation("D101", "10:00", "12:00", Operationtype.Neurosurgery);
        } catch (BookingException e) {
          System.err.println(e.getMessage());
        }
      } else {
        System.out.println("--- Hospital Data Loaded ---");

        // قراءة وعرض الدكاترة ومواعيدهم من الملف
      for (Doctor d : myHospital.getDoctors()) {
        System.out.println("Doctor: " + d.Name + " (ID: " + d.Id + ")");

          if (d.appointments.isEmpty()) {
            System.out.println("  - No appointments scheduled.");
          } else {
            for (Appointment app : d.appointments) {
              String type = (app instanceof Operation) ? "Operation" : "Check-up";
              System.out.println("  [" + type + "] From: " + app.getAppointmentStart() + " To: " + app.getAppointmentEnd());
            }
          }
          System.out.println("---------------------------");
        }
      }
  }
}

