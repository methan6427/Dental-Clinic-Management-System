package com.example.phase4_1220813_122856_1210475;

public class Appointment {
    private int appointmentId;
    private String date;
    private String time;
    private String purpose;
    private int cid; // Patient ID
    private int doctorId; // Doctor ID

    public Appointment(int appointmentId, String date, String time, String purpose, int cid, int doctorId) {
        this.appointmentId = appointmentId;
        this.date = date;
        this.time = time;
        this.purpose = purpose;
        this.cid = cid;
        this.doctorId = doctorId;
    }

    // Getters and Setters
    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }
}
