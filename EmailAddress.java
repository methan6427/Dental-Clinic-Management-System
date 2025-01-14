package com.example.phase4_1220813_122856_1210475;

public class EmailAddress {
	private int ssn;
	private String employeeName;
	private String email;
	private String emailType;

	public EmailAddress(int ssn, String employeeName, String email, String emailType) {
		this.ssn = ssn;
		this.employeeName = employeeName;
		this.email = email;
		this.emailType = emailType;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmailType() {
		return emailType;
	}

	public void setEmailType(String emailType) {
		this.emailType = emailType;
	}

	@Override
	public String toString() {
		return "EmailAddress{" + "ssn=" + ssn + ", employeeName='" + employeeName + '\'' + ", email='" + email + '\''
				+ ", emailType='" + emailType + '\'' + '}';
	}
}