package com.hospital;

public class App {

  public static void main(String[] args) {


    hospital myHospital = new hospital();
//    SurgeonDoctor d1 = new SurgeonDoctor(35,DoctorStatus.Available,"A101","Mohammad") ;
//    myHospital.addDoctor(d1);
//    SurgeonDoctor d2 = new SurgeonDoctor(40,DoctorStatus.Available,"A102","Ali") ;
//    myHospital.addDoctor(d2);
//    myHospital.makeAppointment("A101","10:00","11:30" ) ;
//    myHospital.makeAppointment("A101","12:00","13:00" ) ;


    myHospital.loadDoctorsFromFile();

try {
  myHospital.makeAppointment("A102", "10:00", "10:30");

} catch (BookingException e) {
    // هذا الجزء سيعمل فقط في حال تم رمي Exception من ميثود الحجز
    System.err.println("Booking Error: " + e.getMessage());
  }
    //myHospital.makeOperation("A102", "14:00", "14:30",Operationtype.Neurosurgery);
    //myHospital.makeOperation("A102", "15:00", "15:30",Operationtype.Orthopedicsurgery);

    for (Doctor d : myHospital.getDoctors()) {
      System.out.println("Doctor: " + d.Name);
      for (Appointment app : d.appointments) {
        System.out.println("From: " + app.getAppointmentStart() + " To: " + app.getAppointmentEnd());
      }
    }
  }
}
