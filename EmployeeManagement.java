package com.example.phase4_1220813_122856_1210475;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;

public class EmployeeManagement {
	dataBaseConnection db = new dataBaseConnection();
	ObservableList<Employee> EmployeesList = FXCollections.observableArrayList();
	TableView<Employee> employeeTableView = new TableView<>();

	public void readEmployee() {
		EmployeesList.clear();
		try {
			Connection con = db.getConnection().connectDB();
			String sql = "SELECT * FROM employee";
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				EmployeesList.add(new Employee(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4),
						rs.getInt(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9)));
			}
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		employeeTableView.setItems(EmployeesList);
	}

	public BorderPane createEmployeeManagementLayout(Stage primaryStagee, Scene homeScenee) {
		BorderPane employeeRoot = new BorderPane();
		employeeRoot.setPrefSize(900, 600);
		employeeRoot.setStyle("-fx-background-color: #15919B;");

		Label titleLabel = new Label("Employee Management");
		titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");
		titleLabel.setPadding(new Insets(10, 0, 10, 0));
		titleLabel.setAlignment(Pos.CENTER);

		employeeTableView.setEditable(true);
		employeeTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		employeeTableView.getStyleClass().add("employee-table");

		TableColumn<Employee, Integer> snnColumn = new TableColumn<>("SSN");
		snnColumn.setCellValueFactory(new PropertyValueFactory<>("snn"));

		TableColumn<Employee, String> nameColumn = new TableColumn<>("Name");
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

		TableColumn<Employee, String> dobCol = new TableColumn<>("Date of Birth");
		dobCol.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));

		TableColumn<Employee, String> dojCol = new TableColumn<>("Date of Join");
		dojCol.setCellValueFactory(new PropertyValueFactory<>("dateOfJoin"));

		TableColumn<Employee, Integer> rateCol = new TableColumn<>("Rate");
		rateCol.setCellValueFactory(new PropertyValueFactory<>("rate"));

		TableColumn<Employee, String> emailCol = new TableColumn<>("Email");
		emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

		TableColumn<Employee, String> phoneCol = new TableColumn<>("Phone Number");
		phoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

		TableColumn<Employee, String> addressCol = new TableColumn<>("Address");
		addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));

		TableColumn<Employee, String> typeCol = new TableColumn<>("Type");
		typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

		employeeTableView.getColumns().addAll(snnColumn, nameColumn, dobCol, dojCol, rateCol, emailCol, phoneCol,
				addressCol, typeCol);

		HBox manageButtonsLayout = new HBox(10);
		manageButtonsLayout.setStyle("-fx-padding: 10; -fx-alignment: center; -fx-background-color: #15919B;");

		Button homeEmployeeButton = new Button("Home");
		homeEmployeeButton.setOnAction(e -> primaryStagee.setScene(homeScenee));

		TextField searchEmployeeField = new TextField();
		searchEmployeeField.setPromptText("Enter SSN");
		searchEmployeeField.getStyleClass().add("search-field");

		Button addEmployeeButton = new Button("Add Employee");
		addEmployeeButton.setOnAction(e -> showAddEmployeeDialog());

		Button deleteEmployeeButton = new Button("Delete Employee");
		deleteEmployeeButton.setOnAction(e -> {
			String ssnText = searchEmployeeField.getText();
			if (!ssnText.isEmpty()) {
				int ssn = Integer.parseInt(ssnText);
				deleteEmployee(ssn);
				readEmployee();
			}
		});

		Button searchEmployeeButton = new Button("Search Employee");
		searchEmployeeButton.setOnAction(e -> {
			String ssnText = searchEmployeeField.getText();
			if (!ssnText.isEmpty()) {
				int ssn = Integer.parseInt(ssnText);
				searchEmployee(ssn);
			}
		});

		Button refreshEmployeeButton = new Button("Refresh");
		refreshEmployeeButton.setOnAction(e -> showEditEmployeeDialog());

		Button showSuppliersButton = new Button("Show Suppliers");
		showSuppliersButton.setOnAction(e -> {
			String ssnText = searchEmployeeField.getText();
			if (!ssnText.isEmpty()) {
				int ssn = Integer.parseInt(ssnText);
				showSuppliers(ssn);
			}
		});
		Button managePhonesButton = new Button("Manage Phones");
		managePhonesButton.setOnAction(e -> {
			String ssnText = searchEmployeeField.getText();
			if (!ssnText.isEmpty()) {
				int ssn = Integer.parseInt(ssnText);
				showPhoneNumbersDialog(ssn);
			} else {
				showAlert(Alert.AlertType.WARNING, "Input Error", "Please enter an SSN to manage phones.");
			}
		});

		Button manageAddressesButton = new Button("Manage Addresses");
		manageAddressesButton.setOnAction(e -> {
			String ssnText = searchEmployeeField.getText();
			if (!ssnText.isEmpty()) {
				int ssn = Integer.parseInt(ssnText);
				showAddressesDialog(ssn);
			} else {
				showAlert(Alert.AlertType.WARNING, "Input Error", "Please enter an SSN to manage addresses.");
			}
		});

		Button manageEmailsButton = new Button("Manage Emails");
		manageEmailsButton.setOnAction(e -> {
			String ssnText = searchEmployeeField.getText();
			if (!ssnText.isEmpty()) {
				try {
					int ssn = Integer.parseInt(ssnText);
					showEmails(ssn);
				} catch (NumberFormatException ex) {
					showAlert(Alert.AlertType.WARNING, "Input Error", "Please enter a valid numeric SSN.");
				}
			} else {
				showAlert(Alert.AlertType.WARNING, "Input Error", "Please enter an SSN to manage emails.");
			}
		});
		Button statsButton = new Button("Show Statistics");
		statsButton.setOnAction(e -> showStatistics());

		manageButtonsLayout.getChildren().addAll(managePhonesButton, manageAddressesButton, manageEmailsButton,
				statsButton);

		HBox otherButtonsLayout = new HBox(10);
		otherButtonsLayout.setStyle("-fx-padding: 10; -fx-alignment: center; -fx-background-color: #15919B;");

		otherButtonsLayout.getChildren().addAll(homeEmployeeButton, searchEmployeeField, addEmployeeButton,
				deleteEmployeeButton, searchEmployeeButton, refreshEmployeeButton, showSuppliersButton);

		VBox buttonLayout = new VBox(10);
		buttonLayout.getChildren().addAll(manageButtonsLayout, otherButtonsLayout);
		buttonLayout.setAlignment(Pos.CENTER);

		VBox tableLayout = new VBox(10);
		tableLayout.getChildren().addAll(titleLabel, employeeTableView);
		tableLayout.setAlignment(Pos.CENTER);

		employeeRoot.setCenter(tableLayout);
		employeeRoot.setBottom(buttonLayout);

		return employeeRoot;
	}

	private void showStatistics() {
		Stage statsStage = new Stage();
		statsStage.getIcons().add(new Image(getClass().getResourceAsStream("/statistic.png")));
		statsStage.setTitle("System Statistics");

		VBox statsLayout = new VBox(15);
		statsLayout.setPadding(new Insets(20));
		statsLayout.setAlignment(Pos.TOP_LEFT);

		Label totalEmployeesLabel = new Label();
		Label employeesByTypeLabel = new Label();
		Label averageRateLabel = new Label();
		Label oldestEmployeeLabel = new Label();
		Label youngestEmployeeLabel = new Label();
		Label addressesLabel = new Label();
		Label emailsLabel = new Label();
		Label phonesLabel = new Label();

		String labelStyle = "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;";
		totalEmployeesLabel.setStyle(labelStyle);
		employeesByTypeLabel.setStyle(labelStyle);
		averageRateLabel.setStyle(labelStyle);
		oldestEmployeeLabel.setStyle(labelStyle);
		youngestEmployeeLabel.setStyle(labelStyle);
		addressesLabel.setStyle(labelStyle);
		emailsLabel.setStyle(labelStyle);
		phonesLabel.setStyle(labelStyle);

		try (Connection con = db.getConnection().connectDB()) {
			Statement stmt = con.createStatement();
			ResultSet rs;

			rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM employee");
			if (rs.next()) {
				totalEmployeesLabel.setText("Total Employees: " + rs.getInt("total"));
			}

			rs = stmt.executeQuery("SELECT type, COUNT(*) AS count FROM employee GROUP BY type");
			StringBuilder employeesByType = new StringBuilder("Employees by Type:\n");
			while (rs.next()) {
				employeesByType.append("- ").append(rs.getString("type")).append(": ").append(rs.getInt("count"))
						.append("\n");
			}
			employeesByTypeLabel.setText(employeesByType.toString());

			rs = stmt.executeQuery("SELECT AVG(rate) AS average_rate FROM employee");
			if (rs.next()) {
				averageRateLabel
						.setText("Average Employee Rate: " + String.format("%.2f", rs.getDouble("average_rate")));
			}

			rs = stmt.executeQuery("SELECT Ename FROM employee ORDER BY date_of_birth ASC LIMIT 1");
			if (rs.next()) {
				oldestEmployeeLabel.setText("Oldest Employee: " + rs.getString("Ename"));
			}

			rs = stmt.executeQuery("SELECT Ename FROM employee ORDER BY date_of_birth DESC LIMIT 1");
			if (rs.next()) {
				youngestEmployeeLabel.setText("Youngest Employee: " + rs.getString("Ename"));
			}

			rs = stmt.executeQuery(
					"SELECT (SELECT COUNT(address) FROM employee WHERE address IS NOT NULL AND address != '') AS employee_addresses, (SELECT COUNT(address) FROM employee_addresses) AS additional_addresses");
			if (rs.next()) {
				int employeeAddresses = rs.getInt("employee_addresses");
				int additionalAddresses = rs.getInt("additional_addresses");
				int totalAddresses = employeeAddresses + additionalAddresses;
				addressesLabel.setText("Addresses:\n- Employee Table: " + employeeAddresses
						+ "\n- Additional Addresses: " + additionalAddresses + "\n- Total: " + totalAddresses);
			}

			rs = stmt.executeQuery(
					"SELECT (SELECT COUNT(email) FROM employee WHERE email IS NOT NULL AND email != '') AS employee_emails, (SELECT COUNT(email) FROM email_addresses) AS additional_emails");
			if (rs.next()) {
				int employeeEmails = rs.getInt("employee_emails");
				int additionalEmails = rs.getInt("additional_emails");
				int totalEmails = employeeEmails + additionalEmails;
				emailsLabel.setText("Emails:\n- Employee Table: " + employeeEmails + "\n- Additional Emails: "
						+ additionalEmails + "\n- Total: " + totalEmails);
			}

			rs = stmt.executeQuery(
					"SELECT (SELECT COUNT(phone_number) FROM employee WHERE phone_number IS NOT NULL AND phone_number != '') AS employee_phones, (SELECT COUNT(phone_number) FROM phone_numbers) AS additional_phones");
			if (rs.next()) {
				int employeePhones = rs.getInt("employee_phones");
				int additionalPhones = rs.getInt("additional_phones");
				int totalPhones = employeePhones + additionalPhones;
				phonesLabel.setText("Phone Numbers:\n- Employee Table: " + employeePhones + "\n- Additional Phones: "
						+ additionalPhones + "\n- Total: " + totalPhones);
			}

			rs.close();
			stmt.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Error", "Failed to load statistics.");
		}

		statsLayout.getChildren().addAll(totalEmployeesLabel, employeesByTypeLabel, averageRateLabel,
				oldestEmployeeLabel, youngestEmployeeLabel, addressesLabel, emailsLabel, phonesLabel);

		Scene statsScene = new Scene(statsLayout, 450, 625);
		statsScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
		statsStage.setScene(statsScene);
		statsStage.show();
	}

	private void showAddressesDialog(int ssn) {
		Stage addressStage = new Stage();
		addressStage.getIcons().add(new Image(getClass().getResourceAsStream("/address.png")));

		addressStage.setTitle("Manage Addresses for Employee SSN: " + ssn);

		ObservableList<EmployeeAddress> addressList = FXCollections.observableArrayList();

		String employeeName = getEmployeeNameBySSN(ssn);
		if (employeeName == null || employeeName.isEmpty()) {
			showAlert(Alert.AlertType.WARNING, "Invalid SSN", "The provided SSN does not exist in the employee table.");
			return;
		}

		TableView<EmployeeAddress> addressTable = new TableView<>();

		TableColumn<EmployeeAddress, Integer> ssnColumn = new TableColumn<>("SSN");
		ssnColumn.setCellValueFactory(new PropertyValueFactory<>("ssn"));

		TableColumn<EmployeeAddress, String> nameColumn = new TableColumn<>("Name");
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("employeeName"));

		TableColumn<EmployeeAddress, String> addressColumn = new TableColumn<>("Address");
		addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));

		TableColumn<EmployeeAddress, String> typeColumn = new TableColumn<>("Type");
		typeColumn.setCellValueFactory(new PropertyValueFactory<>("addressType"));

		addressTable.getColumns().addAll(ssnColumn, nameColumn, addressColumn, typeColumn);
		addressTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		ssnColumn.prefWidthProperty().bind(addressTable.widthProperty().multiply(0.25));
		nameColumn.prefWidthProperty().bind(addressTable.widthProperty().multiply(0.25));
		addressColumn.prefWidthProperty().bind(addressTable.widthProperty().multiply(0.25));
		typeColumn.prefWidthProperty().bind(addressTable.widthProperty().multiply(0.25));

		addressTable.setItems(addressList);

		TextField addressField = new TextField();
		addressField.setPromptText("Enter Address");
		addressField.setPrefWidth(300);

		ComboBox<String> typeComboBox = new ComboBox<>();
		typeComboBox.getItems().addAll("Home", "Work");
		typeComboBox.setPromptText("Select Type");
		typeComboBox.setPrefWidth(150);

		Button addAddressButton = new Button("Add Address");
		addAddressButton.setOnAction(e -> {
			String address = addressField.getText();
			String type = typeComboBox.getValue();

			if (address != null && !address.isEmpty() && type != null) {
				boolean addressExistsInTable = addressList.stream()
						.anyMatch(a -> a.getAddress().equals(address) && a.getSsn() == ssn);

				boolean addressExistsInDatabase = isAddressExistsForSSN(ssn, address);

				if (addressExistsInTable || addressExistsInDatabase) {
					showAlert(Alert.AlertType.WARNING, "Duplicate Address",
							"This address is already assigned to this employee.");
				} else {
					try (Connection con = db.getConnection().connectDB()) {
						String sql = "INSERT INTO employee_addresses (employee_ssn, address, address_type) VALUES (?, ?, ?)";
						PreparedStatement pstmt = con.prepareStatement(sql);
						pstmt.setInt(1, ssn);
						pstmt.setString(2, address);
						pstmt.setString(3, type);
						pstmt.executeUpdate();
						addressList.add(new EmployeeAddress(ssn, employeeName, address, type));
						addressField.clear();
						typeComboBox.getSelectionModel().clearSelection();
					} catch (Exception ex) {
						ex.printStackTrace();
						showAlert(Alert.AlertType.ERROR, "Error", "Failed to add address.");
					}
				}
			} else {
				showAlert(Alert.AlertType.WARNING, "Invalid Input", "Please enter a valid address and type.");
			}
		});

		HBox inputLayout = new HBox(10, addressField, typeComboBox, addAddressButton);
		inputLayout.setAlignment(Pos.CENTER);

		VBox layout = new VBox(10, addressTable, inputLayout);
		layout.setPadding(new Insets(20));

		Scene scene = new Scene(layout, 600, 400);
		scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
		addressStage.setScene(scene);
		addressStage.show();

		loadAddresses(ssn, employeeName, addressList);
	}

	private boolean isAddressExistsForSSN(int ssn, String address) {
		try (Connection con = db.getConnection().connectDB()) {
			String sql = "SELECT COUNT(*) FROM employee_addresses WHERE employee_ssn = ? AND address = ?";
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, ssn);
			pstmt.setString(2, address);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next() && rs.getInt(1) > 0) {
				return true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	private void loadAddresses(int ssn, String employeeName, ObservableList<EmployeeAddress> addressList) {
		addressList.clear();
		try (Connection con = db.getConnection().connectDB()) {
			String sqlEmployee = "SELECT address FROM employee WHERE snn = ?";
			PreparedStatement pstmtEmployee = con.prepareStatement(sqlEmployee);
			pstmtEmployee.setInt(1, ssn);
			ResultSet rsEmployee = pstmtEmployee.executeQuery();
			if (rsEmployee.next()) {
				String address = rsEmployee.getString("address");
				if (address != null && !address.isEmpty()) {
					addressList.add(new EmployeeAddress(ssn, employeeName, address, "General"));
				}
			}
			rsEmployee.close();
			pstmtEmployee.close();

			String sqlAddresses = "SELECT address, address_type FROM employee_addresses WHERE employee_ssn = ?";
			PreparedStatement pstmtAddresses = con.prepareStatement(sqlAddresses);
			pstmtAddresses.setInt(1, ssn);
			ResultSet rsAddresses = pstmtAddresses.executeQuery();
			while (rsAddresses.next()) {
				addressList.add(new EmployeeAddress(ssn, employeeName, rsAddresses.getString("address"),
						rsAddresses.getString("address_type")));
			}
			rsAddresses.close();
			pstmtAddresses.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Error", "Failed to load addresses.");
		}
	}

	private void showEmails(int ssn) {
		Stage emailStage = new Stage();
		emailStage.setTitle("Manage Emails for Employee SSN: " + ssn);
		emailStage.getIcons().add(new Image(getClass().getResourceAsStream("/email.png")));

		ObservableList<EmailAddress> emailList = FXCollections.observableArrayList();

		String employeeName = getEmployeeNameBySSN(ssn);
		if (employeeName == null || employeeName.isEmpty()) {
			showAlert(Alert.AlertType.WARNING, "Invalid SSN", "The provided SSN does not exist in the employee table.");
			return;
		}

		TableView<EmailAddress> emailTableView = new TableView<>();

		double columnWidth = 600 / 3;

		TableColumn<EmailAddress, Integer> ssnColumn = new TableColumn<>("SSN");
		ssnColumn.setCellValueFactory(new PropertyValueFactory<>("ssn"));
		ssnColumn.setPrefWidth(columnWidth);

		TableColumn<EmailAddress, String> nameColumn = new TableColumn<>("Name");
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
		nameColumn.setPrefWidth(columnWidth);

		TableColumn<EmailAddress, String> emailColumn = new TableColumn<>("Email");
		emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
		emailColumn.setPrefWidth(columnWidth);

		emailTableView.getColumns().addAll(ssnColumn, nameColumn, emailColumn);
		emailTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		emailTableView.setItems(emailList);

		TextField emailField = new TextField();
		emailField.setPromptText("Enter Email Address");
		emailField.setPrefWidth(300);

		Button addEmailButton = new Button("Add Email");
		addEmailButton.setOnAction(e -> {
			String email = emailField.getText();
			if (email != null && !email.isEmpty() && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
				boolean emailExistsInTable = emailList.stream()
						.anyMatch(em -> em.getEmail().equals(email) && em.getSsn() == ssn);
				boolean emailExistsInDatabase = isPhoneExistsForSSN(ssn, email);

				if (emailExistsInTable || emailExistsInDatabase) {
					showAlert(Alert.AlertType.WARNING, "Duplicate Email",
							"This email is already assigned to this employee.");
				} else {
					try (Connection con = db.getConnection().connectDB()) {
						String sql = "INSERT INTO email_addresses (employee_ssn, email, email_type) VALUES (?, ?, ?)";
						PreparedStatement pstmt = con.prepareStatement(sql);
						pstmt.setInt(1, ssn);
						pstmt.setString(2, email);
						pstmt.setString(3, "Personal");
						pstmt.executeUpdate();

						emailList.add(new EmailAddress(ssn, employeeName, email, "Personal"));

						emailField.clear();
					} catch (Exception ex) {
						ex.printStackTrace();
						showAlert(Alert.AlertType.ERROR, "Error", "Failed to add email.");
					}
				}
			} else {
				showAlert(Alert.AlertType.WARNING, "Invalid Input", "Please enter a valid email address.");
			}
		});

		HBox inputLayout = new HBox(10, emailField, addEmailButton);
		inputLayout.setAlignment(Pos.CENTER);

		VBox layout = new VBox(10, emailTableView, inputLayout);
		layout.setPadding(new Insets(20));

		Scene scene = new Scene(layout, 650, 400);
		scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
		emailStage.setScene(scene);
		emailStage.show();

		loadEmails(ssn, employeeName, emailList);
	}

	private void loadEmails(int ssn, String employeeName, ObservableList<EmailAddress> emailList) {
		emailList.clear();
		try (Connection con = db.getConnection().connectDB()) {
			String sqlEmployee = "SELECT email FROM employee WHERE snn = ?";
			PreparedStatement pstmtEmployee = con.prepareStatement(sqlEmployee);
			pstmtEmployee.setInt(1, ssn);
			ResultSet rsEmployee = pstmtEmployee.executeQuery();
			if (rsEmployee.next()) {
				String email = rsEmployee.getString("email");
				if (email != null && !email.isEmpty()) {
					emailList.add(new EmailAddress(ssn, employeeName, email, "General"));
				}
			}
			rsEmployee.close();
			pstmtEmployee.close();

			String sqlEmails = "SELECT email, email_type FROM email_addresses WHERE employee_ssn = ?";
			PreparedStatement pstmtEmails = con.prepareStatement(sqlEmails);
			pstmtEmails.setInt(1, ssn);
			ResultSet rsEmails = pstmtEmails.executeQuery();
			while (rsEmails.next()) {
				emailList.add(new EmailAddress(ssn, employeeName, rsEmails.getString("email"),
						rsEmails.getString("email_type")));
			}
			rsEmails.close();
			pstmtEmails.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Error", "Failed to load emails.");
		}
	}

	private void showPhoneNumbersDialog(int ssn) {
		Stage phoneStage = new Stage();
		phoneStage.getIcons().add(new Image(getClass().getResourceAsStream("/phone.png")));
		phoneStage.setTitle("Manage Phone Numbers for Employee SSN: " + ssn);

		ObservableList<PhoneNumber> phoneList = FXCollections.observableArrayList();

		String employeeName = getEmployeeNameBySSN(ssn);
		if (employeeName == null || employeeName.isEmpty()) {
			showAlert(Alert.AlertType.WARNING, "Invalid SSN", "The provided SSN does not exist in the employee table.");
			return;
		}

		TableView<PhoneNumber> phoneTable = new TableView<>();

		TableColumn<PhoneNumber, Integer> ssnColumn = new TableColumn<>("SSN");
		ssnColumn.setCellValueFactory(new PropertyValueFactory<>("ssn"));

		TableColumn<PhoneNumber, String> nameColumn = new TableColumn<>("Name");
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("employeeName"));

		TableColumn<PhoneNumber, String> phoneColumn = new TableColumn<>("Phone");
		phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

		TableColumn<PhoneNumber, String> typeColumn = new TableColumn<>("Type");
		typeColumn.setCellValueFactory(new PropertyValueFactory<>("phoneType"));

		phoneTable.getColumns().addAll(ssnColumn, nameColumn, phoneColumn, typeColumn);
		phoneTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		ssnColumn.prefWidthProperty().bind(phoneTable.widthProperty().multiply(0.25));
		nameColumn.prefWidthProperty().bind(phoneTable.widthProperty().multiply(0.25));
		phoneColumn.prefWidthProperty().bind(phoneTable.widthProperty().multiply(0.25));
		typeColumn.prefWidthProperty().bind(phoneTable.widthProperty().multiply(0.25));

		phoneTable.setItems(phoneList);

		TextField phoneField = new TextField();
		phoneField.setPromptText("Enter Phone Number");
		phoneField.setPrefWidth(150);

		ComboBox<String> typeComboBox = new ComboBox<>();
		typeComboBox.getItems().addAll("Personal", "Work");
		typeComboBox.setPromptText("Select Type");
		typeComboBox.setPrefWidth(100);

		Button addPhoneButton = new Button("Add Phone");
		addPhoneButton.setOnAction(e -> {
			String phone = phoneField.getText();
			String type = typeComboBox.getValue();

			if (phone != null && phone.matches("\\d{10}") && type != null) {
				boolean phoneExistsInTable = phoneList.stream()
						.anyMatch(p -> p.getPhoneNumber().equals(phone) && p.getSsn() == ssn);

				boolean phoneExistsInDatabase = isPhoneExistsForSSN(ssn, phone);

				if (phoneExistsInTable || phoneExistsInDatabase) {
					showAlert(Alert.AlertType.WARNING, "Duplicate Phone",
							"This phone number is already assigned to this employee.");
				} else {
					try (Connection con = db.getConnection().connectDB()) {
						String sql = "INSERT INTO phone_numbers (employee_ssn, phone_number, phone_type) VALUES (?, ?, ?)";
						PreparedStatement pstmt = con.prepareStatement(sql);
						pstmt.setInt(1, ssn);
						pstmt.setString(2, phone);
						pstmt.setString(3, type);
						pstmt.executeUpdate();
						phoneList.add(new PhoneNumber(ssn, employeeName, phone, type));
						phoneField.clear();
						typeComboBox.getSelectionModel().clearSelection();
					} catch (Exception ex) {
						ex.printStackTrace();
						showAlert(Alert.AlertType.ERROR, "Error", "Failed to add phone number.");
					}
				}
			} else {
				showAlert(Alert.AlertType.WARNING, "Invalid Input",
						"Please enter a valid 10-digit phone number and type.");
			}
		});

		Button deletePhoneButton = new Button("Delete Phone");
		deletePhoneButton.setOnAction(e -> {
			PhoneNumber selectedPhone = phoneTable.getSelectionModel().getSelectedItem();
			if (selectedPhone != null) {
				try (Connection con = db.getConnection().connectDB()) {
					String sql = "DELETE FROM phone_numbers WHERE phone_number = ? AND employee_ssn = ?";
					PreparedStatement pstmt = con.prepareStatement(sql);
					pstmt.setString(1, selectedPhone.getPhoneNumber());
					pstmt.setInt(2, ssn);
					pstmt.executeUpdate();
					phoneList.remove(selectedPhone);
					showAlert(Alert.AlertType.INFORMATION, "Success", "Phone number deleted successfully.");
				} catch (Exception ex) {
					ex.printStackTrace();
					showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete phone number.");
				}
			} else {
				showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a phone number to delete.");
			}
		});

		HBox inputLayout = new HBox(10, phoneField, typeComboBox, addPhoneButton, deletePhoneButton);
		inputLayout.setAlignment(Pos.CENTER);

		VBox layout = new VBox(10, phoneTable, inputLayout);
		layout.setPadding(new Insets(20));

		Scene scene = new Scene(layout, 600, 400);
		scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
		phoneStage.setScene(scene);
		phoneStage.show();

		loadPhoneNumbers(ssn, employeeName, phoneList);
	}

	private boolean isPhoneExistsForSSN(int ssn, String phone) {
		try (Connection con = db.getConnection().connectDB()) {
			String sql = "SELECT COUNT(*) FROM phone_numbers WHERE employee_ssn = ? AND phone_number = ?";
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, ssn);
			pstmt.setString(2, phone);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next() && rs.getInt(1) > 0) {
				return true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	private String getEmployeeNameBySSN(int ssn) {
		String name = "";
		try (Connection con = db.getConnection().connectDB()) {
			String sql = "SELECT Ename FROM employee WHERE snn = ?";
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, ssn);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				name = rs.getString("Ename");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Error", "Failed to retrieve employee name.");
		}
		return name;
	}

	private void loadPhoneNumbers(int ssn, String employeeName, ObservableList<PhoneNumber> phoneList) {
		phoneList.clear();
		try (Connection con = db.getConnection().connectDB()) {
			String sqlEmployee = "SELECT phone_number FROM employee WHERE snn = ?";
			PreparedStatement pstmtEmployee = con.prepareStatement(sqlEmployee);
			pstmtEmployee.setInt(1, ssn);
			ResultSet rsEmployee = pstmtEmployee.executeQuery();
			if (rsEmployee.next()) {
				String phone = rsEmployee.getString("phone_number");
				if (phone != null && !phone.isEmpty()) {
					phoneList.add(new PhoneNumber(ssn, employeeName, phone, "General"));
				}
			}
			rsEmployee.close();
			pstmtEmployee.close();

			String sqlPhones = "SELECT phone_number, phone_type FROM phone_numbers WHERE employee_ssn = ?";
			PreparedStatement pstmtPhones = con.prepareStatement(sqlPhones);
			pstmtPhones.setInt(1, ssn);
			ResultSet rsPhones = pstmtPhones.executeQuery();
			while (rsPhones.next()) {
				phoneList.add(new PhoneNumber(ssn, employeeName, rsPhones.getString("phone_number"),
						rsPhones.getString("phone_type")));
			}
			rsPhones.close();
			pstmtPhones.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Error", "Failed to load phone numbers.");
		}
	}

	private void showEditEmployeeDialog() {
		Stage editEmpStage = new Stage();
		VBox editEmpLayout = new VBox(10);
		editEmpLayout.setPadding(new Insets(20));

		editEmpStage.setTitle("Edit Employee");
		editEmpStage.getIcons().add(new Image(getClass().getResourceAsStream("/88.png")));

		TextField ssnField = new TextField();
		ssnField.setPromptText("Enter SSN to Search");
		ssnField.getStyleClass().add("input-field");

		Button searchButton = new Button("Search");
		searchButton.getStyleClass().add("action-button");

		TextField nameField = new TextField();
		nameField.setPromptText("Name");

		DatePicker dbirthField = new DatePicker();
		dbirthField.setPromptText("Date of Birth");

		DatePicker djoinField = new DatePicker();
		djoinField.setPromptText("Date of Join");

		TextField rateField = new TextField();
		rateField.setPromptText("Rate");

		TextField addressField = new TextField();
		addressField.setPromptText("Address");

		TextField emailField = new TextField();
		emailField.setPromptText("Email");

		TextField phoneNumberField = new TextField();
		phoneNumberField.setPromptText("Phone Number");

		ComboBox<String> typeComboBox = new ComboBox<>();
		typeComboBox.getItems().addAll("worker", "manager", "accountant");
		typeComboBox.setPromptText("Select Type");

		Button saveButton = new Button("Save");
		saveButton.getStyleClass().add("action-button");

		searchButton.setOnAction(e -> {
			try {
				int ssn = Integer.parseInt(ssnField.getText());
				if (String.valueOf(ssn).length() > 10) {
					showAlert(Alert.AlertType.WARNING, "Input Error", "SSN should not exceed 10 digits.");
					return;
				}

				Connection con = db.getConnection().connectDB();
				String sql = "SELECT * FROM employee WHERE snn = ?";
				PreparedStatement pstmt = con.prepareStatement(sql);
				pstmt.setInt(1, ssn);
				ResultSet rs = pstmt.executeQuery();

				if (rs.next()) {
					nameField.setText(rs.getString("Ename"));
					dbirthField.setValue(LocalDate.parse(rs.getString("date_of_birth")));
					djoinField.setValue(LocalDate.parse(rs.getString("date_of_join")));
					rateField.setText(String.valueOf(rs.getInt("rate")));
					addressField.setText(rs.getString("address"));
					emailField.setText(rs.getString("email"));
					phoneNumberField.setText(rs.getString("phone_number"));
					typeComboBox.setValue(rs.getString("type"));
				} else {
					showAlert(Alert.AlertType.WARNING, "Not Found", "Employee with SSN " + ssn + " not found.");
				}

				rs.close();
				pstmt.close();
				con.close();
			} catch (NumberFormatException ex) {
				showAlert(Alert.AlertType.WARNING, "Input Error", "Please enter a valid numeric SSN.");
			} catch (Exception ex) {
				ex.printStackTrace();
				showAlert(Alert.AlertType.ERROR, "Error", "Failed to retrieve employee data.");
			}
		});

		saveButton.setOnAction(e -> {
			try {
				if (ssnField.getText().isEmpty() || !ssnField.getText().matches("\\d+")) {
					showAlert(Alert.AlertType.WARNING, "Input Error", "SSN must be numeric and cannot be empty.");
					return;
				}
				int ssn = Integer.parseInt(ssnField.getText());
				if (String.valueOf(ssn).length() > 10) {
					showAlert(Alert.AlertType.WARNING, "Input Error", "SSN should not exceed 10 digits.");
					return;
				}

				String name = nameField.getText();
				if (name.isEmpty()) {
					showAlert(Alert.AlertType.WARNING, "Input Error", "Name cannot be empty.");
					return;
				}

				if (dbirthField.getValue() == null || djoinField.getValue() == null) {
					showAlert(Alert.AlertType.WARNING, "Input Error",
							"Please select valid dates for Date of Birth and Date of Join.");
					return;
				}
				String dateOfBirth = dbirthField.getValue().toString();
				String dateOfJoin = djoinField.getValue().toString();

				if (rateField.getText().isEmpty() || !rateField.getText().matches("\\d+")) {
					showAlert(Alert.AlertType.WARNING, "Input Error",
							"Rate must be a numeric value and cannot be empty.");
					return;
				}
				int rate = Integer.parseInt(rateField.getText());

				String address = addressField.getText();
				if (address.isEmpty()) {
					showAlert(Alert.AlertType.WARNING, "Input Error", "Address cannot be empty.");
					return;
				}

				String email = emailField.getText();
				String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
				if (!email.matches(emailRegex)) {
					showAlert(Alert.AlertType.WARNING, "Input Error",
							"Invalid email format. Please enter a valid email address.");
					return;
				}

				String phoneNumber = phoneNumberField.getText();
				if (phoneNumber.length() != 10 || !phoneNumber.matches("\\d+")) {
					showAlert(Alert.AlertType.WARNING, "Input Error",
							"Phone number must be exactly 10 digits and contain only numbers.");
					return;
				}

				String type = typeComboBox.getValue();
				if (type == null || type.isEmpty()) {
					showAlert(Alert.AlertType.WARNING, "Input Error", "Please select a valid employee type.");
					return;
				}

				Connection con = db.getConnection().connectDB();
				String sql = "UPDATE employee SET Ename = ?, date_of_birth = ?, date_of_join = ?, rate = ?, address = ?, email = ?, phone_number = ?, type = ? WHERE snn = ?";
				PreparedStatement pstmt = con.prepareStatement(sql);
				pstmt.setString(1, name);
				pstmt.setString(2, dateOfBirth);
				pstmt.setString(3, dateOfJoin);
				pstmt.setInt(4, rate);
				pstmt.setString(5, address);
				pstmt.setString(6, email);
				pstmt.setString(7, phoneNumber);
				pstmt.setString(8, type);
				pstmt.setInt(9, ssn);

				int rowsUpdated = pstmt.executeUpdate();
				con.close();

				if (rowsUpdated > 0) {
					showAlert(Alert.AlertType.INFORMATION, "Success", "Employee details updated successfully.");
					readEmployee();
					editEmpStage.close();
				} else {
					showAlert(Alert.AlertType.WARNING, "Update Failed", "No employee found with the provided SSN.");
				}
			} catch (NumberFormatException ex) {
				showAlert(Alert.AlertType.WARNING, "Input Error", "Please enter valid numeric values.");
			} catch (Exception ex) {
				ex.printStackTrace();
				showAlert(Alert.AlertType.ERROR, "Error", "Failed to update employee details.");
			}
		});

		editEmpLayout.getChildren().addAll(ssnField, searchButton, nameField, dbirthField, djoinField, rateField,
				addressField, emailField, phoneNumberField, typeComboBox, saveButton);

		Scene editEmpScene = new Scene(editEmpLayout, 400, 415);
		editEmpScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
		editEmpStage.setScene(editEmpScene);
		editEmpStage.show();
	}

	private void deleteEmployee(int ssn) {
		try {
			Connection con = db.getConnection().connectDB();
			String sql = "DELETE FROM employee WHERE snn=?";
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, ssn);

			int affectedRows = pstmt.executeUpdate();

			pstmt.close();
			con.close();

			if (affectedRows > 0) {
				showAlert(Alert.AlertType.INFORMATION, "Success",
						"Employee with SSN " + ssn + " was successfully deleted.");
			} else {
				showAlert(Alert.AlertType.WARNING, "Not Found",
						"Employee with SSN " + ssn + " was not found in the database.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Error",
					"An error occurred while trying to delete the employee. Please try again.");
		}
	}

	private void searchEmployee(int ssn) {
		Stage employeeStage = new Stage();
		employeeStage.setTitle("Search Results for Employee SSN: " + ssn);
		employeeStage.getIcons().add(new Image(getClass().getResourceAsStream("/99.png")));

		TableView<Employee> employeeTableView = new TableView<>();
		employeeTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		TableColumn<Employee, Integer> ssnCol = new TableColumn<>("SSN");
		ssnCol.setCellValueFactory(new PropertyValueFactory<>("snn"));

		TableColumn<Employee, String> nameCol = new TableColumn<>("Name");
		nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

		TableColumn<Employee, String> dobCol = new TableColumn<>("Date of Birth");
		dobCol.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));

		TableColumn<Employee, String> dojCol = new TableColumn<>("Date of Join");
		dojCol.setCellValueFactory(new PropertyValueFactory<>("dateOfJoin"));

		TableColumn<Employee, Integer> rateCol = new TableColumn<>("Rate");
		rateCol.setCellValueFactory(new PropertyValueFactory<>("rate"));

		TableColumn<Employee, String> addressCol = new TableColumn<>("Address");
		addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));

		TableColumn<Employee, String> emailCol = new TableColumn<>("Email");
		emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

		TableColumn<Employee, String> phoneCol = new TableColumn<>("Phone Number");
		phoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

		TableColumn<Employee, String> typeCol = new TableColumn<>("Type");
		typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

		employeeTableView.getColumns().addAll(ssnCol, nameCol, dobCol, dojCol, rateCol, addressCol, emailCol, phoneCol,
				typeCol);

		ObservableList<Employee> employeeList = FXCollections.observableArrayList();

		try {
			Connection con = db.getConnection().connectDB();
			String sql = "SELECT * FROM employee WHERE snn=?";
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, ssn);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				employeeList.add(new Employee(rs.getInt("snn"), rs.getString("Ename"), rs.getString("date_of_birth"),
						rs.getString("date_of_join"), rs.getInt("rate"), rs.getString("address"), rs.getString("email"),
						rs.getString("phone_number"), rs.getString("type")));
			} else {
				showAlert(Alert.AlertType.WARNING, "Employee Not Found",
						"The employee with SSN " + ssn + " was not found.");
				return;
			}

			rs.close();
			pstmt.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		employeeTableView.setItems(employeeList);

		VBox layout = new VBox(10, employeeTableView);
		layout.setPadding(new Insets(10));

		Scene scene = new Scene(layout, 800, 400);
		scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

		employeeStage.setScene(scene);
		employeeStage.show();
	}

	private void showAddEmployeeDialog() {
		Stage addEmpStage = new Stage();
		VBox addEmpLayout = new VBox(10);
		addEmpLayout.setStyle("-fx-padding: 20;");
		addEmpStage.getIcons().add(new Image(getClass().getResourceAsStream("/people.png")));

		TextField ssnField = new TextField();
		ssnField.setPromptText("SSN");
		ssnField.getStyleClass().add("input-field");

		TextField nameField = new TextField();
		nameField.setPromptText("Name");
		nameField.getStyleClass().add("input-field");

		DatePicker dbirthField = new DatePicker();
		dbirthField.setPromptText("Date of Birth");
		dbirthField.getStyleClass().add("date-picker");

		DatePicker djoinField = new DatePicker();
		djoinField.setPromptText("Date of Join");
		djoinField.getStyleClass().add("date-picker");

		TextField rateField = new TextField();
		rateField.setPromptText("Rate");
		rateField.getStyleClass().add("input-field");

		TextField addressField = new TextField();
		addressField.setPromptText("Address");
		addressField.getStyleClass().add("input-field");

		TextField emailField = new TextField();
		emailField.setPromptText("Email");
		emailField.getStyleClass().add("input-field");

		TextField phoneNumberField = new TextField();
		phoneNumberField.setPromptText("Phone Number");
		phoneNumberField.getStyleClass().add("input-field");

		ComboBox<String> typeComboBox = new ComboBox<>();
		typeComboBox.getItems().addAll("worker", "manager", "accountant");
		typeComboBox.setPromptText("Select Type");
		typeComboBox.getStyleClass().add("combo-box");

		Button addBtn = new Button("Add");
		addBtn.getStyleClass().add("action-button");
		addBtn.setOnAction(e -> {
			try {
				if (!isNumeric(ssnField.getText())) {
					showAlert(Alert.AlertType.ERROR, "Invalid Input", "SSN must be a numeric value.");
					return;
				}

				if (isSSNExists(Integer.parseInt(ssnField.getText()))) {
					showAlert(Alert.AlertType.ERROR, "Duplicate SSN", "The SSN already exists in the database.");
					return;
				}

				if (!isNumeric(rateField.getText())) {
					showAlert(Alert.AlertType.ERROR, "Invalid Input", "Rate must be a numeric value.");
					return;
				}

				if (!isValidPhoneNumber(phoneNumberField.getText())) {
					showAlert(Alert.AlertType.ERROR, "Invalid Input", "Phone Number must be exactly 10 digits.");
					return;
				}

				if (!isValidEmail(emailField.getText())) {
					showAlert(Alert.AlertType.ERROR, "Invalid Input",
							"Invalid Email Format. Please enter a valid email address.");
					return;
				}

				if (typeComboBox.getValue() == null) {
					showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please select a type.");
					return;
				}

				int ssn = Integer.parseInt(ssnField.getText());
				String name = nameField.getText();
				String dateOfBirth = dbirthField.getValue() != null ? dbirthField.getValue().toString() : "";
				String dateOfJoin = djoinField.getValue() != null ? djoinField.getValue().toString() : "";
				int rate = Integer.parseInt(rateField.getText());
				String address = addressField.getText();
				String email = emailField.getText();
				String phoneNumber = phoneNumberField.getText();
				String type = typeComboBox.getValue();

				if (name.isEmpty() || dateOfBirth.isEmpty() || dateOfJoin.isEmpty() || address.isEmpty()
						|| email.isEmpty() || phoneNumber.isEmpty()) {
					showAlert(Alert.AlertType.ERROR, "Invalid Input", "All fields must be filled.");
					return;
				}

				Connection con = db.getConnection().connectDB();
				String sql = "INSERT INTO employee(snn, Ename, date_of_birth, date_of_join, rate, address, email, phone_number, type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
				PreparedStatement pstmt = con.prepareStatement(sql);
				pstmt.setInt(1, ssn);
				pstmt.setString(2, name);
				pstmt.setString(3, dateOfBirth);
				pstmt.setString(4, dateOfJoin);
				pstmt.setInt(5, rate);
				pstmt.setString(6, address);
				pstmt.setString(7, email);
				pstmt.setString(8, phoneNumber);
				pstmt.setString(9, type);
				pstmt.executeUpdate();

				EmployeesList
						.add(new Employee(ssn, name, dateOfBirth, dateOfJoin, rate, address, email, phoneNumber, type));
				con.close();
				addEmpStage.close();
			} catch (Exception ex) {
				ex.printStackTrace();
				showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while adding the employee.");
			}
		});

		addEmpLayout.getChildren().addAll(ssnField, nameField, dbirthField, djoinField, rateField, addressField,
				emailField, phoneNumberField, typeComboBox, addBtn);

		Scene addEmpScene = new Scene(addEmpLayout, 400, 390);
		addEmpScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
		addEmpStage.setScene(addEmpScene);
		addEmpStage.setTitle("Add Employee");
		addEmpStage.show();
	}

	private boolean isValidEmail(String text) {
		if (text == null || text.isEmpty()) {
			return false;
		}
		String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
		return text.matches(emailRegex);
	}

	private boolean isNumeric(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private boolean isValidPhoneNumber(String phoneNumber) {
		return phoneNumber.matches("\\d{10}");
	}

	private boolean isSSNExists(int ssn) {
		boolean exists = false;
		try {
			Connection con = db.getConnection().connectDB();
			String sql = "SELECT COUNT(*) FROM employee WHERE snn = ?";
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, ssn);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next() && rs.getInt(1) > 0) {
				exists = true;
			}
			rs.close();
			pstmt.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return exists;
	}

	private void showSuppliers(int ssn) {
	    Stage suppliersStage = new Stage();
	    suppliersStage.setTitle("Suppliers for Employee SSN: " + ssn);
	    suppliersStage.getIcons().add(new Image(getClass().getResourceAsStream("/people.png")));

	    TableView<Supplier> supplierTableView = new TableView<>();
	    supplierTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

	    TableColumn<Supplier, Integer> idCol = new TableColumn<>("ID");
	    idCol.setCellValueFactory(new PropertyValueFactory<>("s_id"));

	    TableColumn<Supplier, String> nameCol = new TableColumn<>("Name");
	    nameCol.setCellValueFactory(new PropertyValueFactory<>("s_name"));

	    TableColumn<Supplier, String> emailCol = new TableColumn<>("Email");
	    emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

	    TableColumn<Supplier, String> phoneCol = new TableColumn<>("Phone Number");
	    phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone_number"));

	    supplierTableView.getColumns().addAll(idCol, nameCol, emailCol, phoneCol);

	    ObservableList<Supplier> supplierList = FXCollections.observableArrayList();

	    try {
	        Connection con = db.getConnection().connectDB();
	        String sql = "SELECT * FROM supplier WHERE employee_ssn=?";
	        PreparedStatement pstmt = con.prepareStatement(sql);
	        pstmt.setInt(1, ssn);
	        ResultSet rs = pstmt.executeQuery();

	        while (rs.next()) {
	            supplierList.add(new Supplier(rs.getInt("s_id"), rs.getString("s_name"), rs.getString("email"),
	                    rs.getString("phone_number"), ssn));
	        }

	        rs.close();
	        pstmt.close();
	        con.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    if (supplierList.isEmpty()) {
	        showAlert(Alert.AlertType.WARNING, "No Suppliers",
	                "No suppliers found for the employee with SSN " + ssn + ".");
	        return;
	    }

	    supplierTableView.setItems(supplierList);
	    VBox layout = new VBox(10, supplierTableView);
	    layout.setPadding(new Insets(10));
	    Scene scene = new Scene(layout, 600, 400);
	    scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
	    suppliersStage.setScene(scene);
	    suppliersStage.show();
	}


	private void showAlert(Alert.AlertType alertType, String title, String content) {
		Stage alertStage = new Stage();
		alertStage.initModality(Modality.APPLICATION_MODAL);
		alertStage.setTitle(title);

		alertStage.getIcons().add(new Image(getClass().getResourceAsStream("/11.png")));

		Label titleLabel = new Label(title);
		titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

		HBox titleBox = new HBox(10, titleLabel);
		titleBox.setAlignment(Pos.CENTER);

		Label messageLabel = new Label(content);
		messageLabel.setWrapText(true);
		messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");

		Button closeButton = new Button("Close");
		closeButton.setStyle(
				"-fx-background-color: linear-gradient(#C3C7F4, #15919B); -fx-text-fill: white; -fx-font-weight: bold;");
		closeButton.setOnAction(e -> alertStage.close());

		VBox layout = new VBox(15, titleBox, messageLabel, closeButton);
		layout.setPadding(new Insets(20));
		layout.setAlignment(Pos.CENTER);
		layout.setStyle("-fx-background-color: linear-gradient(#15919B, #C3C7F4); -fx-border-radius: 10;");

		Scene scene = new Scene(layout, 400, 200);
		alertStage.setScene(scene);
		alertStage.showAndWait();
	}
}