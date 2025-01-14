package com.example.phase4_1220813_122856_1210475;

import java.time.LocalDate;

public class Stock {
	private int stockId;
	private String shelfLocation;
	private LocalDate lastUpdate;
	private int employeeSsn;

	public Stock(int stockId, String shelfLocation, LocalDate lastUpdate, int employeeSsn) {
		this.stockId = stockId;
		this.shelfLocation = shelfLocation;
		this.lastUpdate = lastUpdate;
		this.employeeSsn = employeeSsn;
	}

	public Stock(String shelfLocation, LocalDate lastUpdate, int employeeSsn) {
		this.shelfLocation = shelfLocation;
		this.lastUpdate = lastUpdate;
		this.employeeSsn = employeeSsn;
	}

	public int getEmployeeSsn() {
		return employeeSsn;
	}

	public void setEmployeeSsn(int employeeSsn) {
		this.employeeSsn = employeeSsn;
	}

	public int getStockId() {
		return stockId;
	}

	public void setStockId(int stockId) {
		this.stockId = stockId;
	}

	public String getShelfLocation() {
		return shelfLocation;
	}

	public void setShelfLocation(String shelfLocation) {
		this.shelfLocation = shelfLocation;
	}

	public LocalDate getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(LocalDate lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	@Override
	public String toString() {
		return "Stock{" + "stockId=" + stockId + ", shelfLocation='" + shelfLocation + '\'' + ", lastUpdate="
				+ lastUpdate + ", employeeSsn=" + employeeSsn + '}';
	}
}