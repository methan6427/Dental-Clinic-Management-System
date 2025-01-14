package com.example.phase4_1220813_122856_1210475;

public class PhoneEntry {
	private int s_id;
	private String supplierName;
	private String phone;
	private String type;

	public PhoneEntry(int s_id, String supplierName, String phone, String type) {
		this.s_id = s_id;
		this.supplierName = supplierName;
		this.phone = phone;
		this.type = type;
	}

	public int getS_id() {
		return s_id;
	}

	public void sets_id(int s_id) {
		this.s_id = s_id;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getType() {
		return type;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "PhoneEntry [s_id=" + s_id + ", supplierName=" + supplierName + ", phone=" + phone + ", type=" + type
				+ "]";
	}
}