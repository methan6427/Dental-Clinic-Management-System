package com.example.phase4_1220813_122856_1210475;

public class Supplier {
	private int s_id;
	private String s_name;
	private String email;
	private String phone_number;
	private int employee_ssn;

	public Supplier(int s_id, String s_name, String email, String phone_number, int employee_ssn) {
		this.s_id = s_id;
		this.s_name = s_name;
		this.email = email;
		this.phone_number = phone_number;
		this.employee_ssn = employee_ssn;
	}

	public int getS_id() {
		return s_id;
	}

	public void setS_id(int s_id) {
		this.s_id = s_id;
	}

	public String getS_name() {
		return s_name;
	}

	public void setS_name(String s_name) {
		this.s_name = s_name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone_number() {
		return phone_number;
	}

	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}

	public int getEmployee_ssn() {
		return employee_ssn;
	}

	public void setEmployee_ssn(int employee_ssn) {
		this.employee_ssn = employee_ssn;
	}

	@Override
	public String toString() {
		return "Supplier{" + "s_id=" + s_id + ", s_name='" + s_name + '\'' + ", email='" + email + '\''
				+ ", phone_number='" + phone_number + '\'' + ", employee_ssn=" + employee_ssn + '}';
	}
}