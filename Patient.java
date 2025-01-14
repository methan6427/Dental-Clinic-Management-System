package com.example.phase4_1220813_122856_1210475;

public class Patient {
    private int cid;
    private String fullName;
    private String gender;
    private String email;
    private String phoneNumber;
    private String address;
    private String dateOfBirth;

    // Constructor
    public Patient(int cid, String fullName, String gender, String email, String phoneNumber, String address, String dateOfBirth) {
        this.cid = cid;
        this.fullName = fullName;
        this.gender = gender;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
    }

    // Getters
    public int getCid() {
        return cid;
    }

    public String getFullName() {
        return fullName;
    }

    public String getGender() {
        return gender;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    // Setters (if needed)
    public void setCid(int cid) {
        this.cid = cid;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
