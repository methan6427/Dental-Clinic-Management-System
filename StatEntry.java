package com.example.phase4_1220813_122856_1210475;

public class StatEntry {
	private int id;
	private String name;
	private int value;

	public StatEntry(int id, String name, int value) {
		this.id = id;
		this.name = name;
		this.value = value;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}