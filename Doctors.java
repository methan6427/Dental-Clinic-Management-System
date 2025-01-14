package com.example.phase4_1220813_122856_1210475;

public class Doctors {
    private int doctor_id;
    private String name;
    private String speciality;
    private long phonenumber;
    private String email;
    private String date;

    public Doctors(int doctor_id, String name, String speciality, long phonenumber, String email, String date) {
        this.doctor_id = doctor_id;
        this.name = name;
        this.speciality = speciality;
        this.phonenumber = phonenumber;
        this.email = email;
        this.date = date;
    }

    public int getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(int doctor_id) {
        this.doctor_id = doctor_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public long getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(long phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Doctors{" +
                "doctor_id=" + doctor_id +
                ", name='" + name + '\'' +
                ", speciality='" + speciality + '\'' +
                ", phonenumber=" + phonenumber +
                ", email='" + email + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
