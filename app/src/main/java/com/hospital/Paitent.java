package com.hospital;

import java.util.ArrayList;

public class Paitent {
    private String Id;
    private String Name;
    private int Age;
    private String phoneNumber;
    private ArrayList <Appointment> Visit=new ArrayList <>();
    public Paitent(String id, String name, int age, String phoneNumber) {
        this.Id = id;
        this.Name = name;
        this.Age = age;
        this.phoneNumber = phoneNumber;

    }

    public void Addvisit(Appointment    appointment){

        Visit.add(appointment);

    }
    public String getId() {
        return Id;
    }

    public String getName() {
        return Name;
    }

    public int getAge() {
        return Age;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
