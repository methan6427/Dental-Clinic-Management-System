package com.example.phase4_1220813_122856_1210475;

public class Email {
	private int s_id;
	private String supplierName;
	private String email;

	public Email(int s_id, String supplierName, String email) {
		this.s_id = s_id;
		this.supplierName = supplierName;
		this.email = email;
	}

	public int getS_id() {
		return s_id;
	}

	public void setS_id(int s_id) {
		this.s_id = s_id;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "Email{" + "s_id=" + s_id + ", supplierName='" + supplierName + '\'' + ", email='" + email + '\'' + '}';
	}
}