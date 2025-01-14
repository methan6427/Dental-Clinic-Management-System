package com.example.phase4_1220813_122856_1210475;

public class PhoneNumber {
    private int ssn;
    private String employeeName;
    private String phoneNumber;
    private String phoneType;

    public PhoneNumber(int ssn, String employeeName, String phoneNumber, String phoneType) {
        this.ssn = ssn;
        this.employeeName = employeeName;
        this.phoneNumber = phoneNumber;
        this.phoneType = phoneType;
    }

    public int getSsn() {
        return ssn;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPhoneType() {
        return phoneType;
    }
}
