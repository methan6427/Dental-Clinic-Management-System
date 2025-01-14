package com.example.phase4_1220813_122856_1210475;

public class EmployeeAddress {
	private int ssn;
	private String employeeName;
	private String address;
	private String addressType;

	public EmployeeAddress(int ssn, String employeeName, String address, String addressType) {
		this.ssn = ssn;
		this.employeeName = employeeName;
		this.address = address;
		this.addressType = addressType;
	}

	public int getSsn() {
		return ssn;
	}

	public void setSsn(int ssn) {
		this.ssn = ssn;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddressType() {
		return addressType;
	}

	public void setAddressType(String addressType) {
		this.addressType = addressType;
	}

	@Override
	public String toString() {
		return "EmployeeAddress{" + "ssn=" + ssn + ", employeeName='" + employeeName + '\'' + ", address='" + address
				+ '\'' + ", addressType='" + addressType + '\'' + '}';
	}
}