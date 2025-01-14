package com.example.phase4_1220813_122856_1210475;

public class dataBaseConnection {

	private String dbUsername = "root";
	private String dbPassword = "mysql";
	private String URL = "127.0.0.1";
	private String port = "3306";
	private String dbName = "DBproject";
	private DBConn connection;

	public dataBaseConnection(String dbUsername, String dbPassword, String URL, String port, String dbName) {
		this.connection = new DBConn(URL, port, dbName, dbUsername, dbPassword);
	}

	public dataBaseConnection() {
		dbUsername = "root";
		dbPassword = "Adam@6427";
		URL = "127.0.0.1";
		port = "3306";
		dbName = "DBproject";
		this.connection = new DBConn(URL, port, dbName, dbUsername, dbPassword);
	}

	public String getDbUsername() {
		return dbUsername;
	}

	public void setDbUsername(String dbUsername) {
		this.dbUsername = dbUsername;
	}

	public String getDbPassword() {
		return dbPassword;
	}

	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String URL) {
		this.URL = URL;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public DBConn getCon() {
		return connection;
	}

	public DBConn getConnection() {
		return connection;
	}
}