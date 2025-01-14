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

import java.sql.*;

public class SupplierManagement {
	dataBaseConnection db = new dataBaseConnection();
	ObservableList<Supplier> supplierList = FXCollections.observableArrayList();
	TableView<Supplier> supplierTableView = new TableView<>();

	public void readSublayer() {
		supplierList.clear();
		try {
			Connection con = db.getConnection().connectDB();
			String sql = "SELECT * FROM supplier";
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				supplierList.add(
						new Supplier(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5)));
			}
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		supplierTableView.setItems(supplierList);
	}

	public BorderPane createSupplierManagementLayout(Stage primaryStage, Scene homeScene) {
		BorderPane supplierRoot = new BorderPane();
		supplierRoot.setPrefSize(800, 600);

		Label titleLabel = new Label("Supplier Management");
		titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
		VBox titleBox = new VBox(titleLabel);
		titleBox.setAlignment(Pos.CENTER);
		titleBox.setPadding(new Insets(10));
		titleBox.setStyle("-fx-background-color: #15919B;");

		TableColumn<Supplier, Integer> s_idCol = new TableColumn<>("ID");
		s_idCol.setCellValueFactory(new PropertyValueFactory<>("s_id"));

		TableColumn<Supplier, String> s_nameCol = new TableColumn<>("Name");
		s_nameCol.setCellValueFactory(new PropertyValueFactory<>("s_name"));

		TableColumn<Supplier, String> emailCol = new TableColumn<>("Email");
		emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

		TableColumn<Supplier, String> phoneCol = new TableColumn<>("Phone Number");
		phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone_number"));

		TableColumn<Supplier, Integer> employeeSSNCol = new TableColumn<>("Employee SSN");
		employeeSSNCol.setCellValueFactory(new PropertyValueFactory<>("employee_ssn"));

		supplierTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		supplierTableView.getColumns().addAll(s_idCol, s_nameCol, emailCol, phoneCol, employeeSSNCol);
		supplierTableView.setItems(supplierList);

		HBox manageButtonsLayout = new HBox(10);
		manageButtonsLayout.setAlignment(Pos.CENTER);
		manageButtonsLayout.setPadding(new Insets(10));

		Button managePhoneButton = new Button("Manage Phone");

		Button manageEmailButton = new Button("Manage Emails");

		Button statisticsButton = new Button("Statistics");
		statisticsButton.setOnAction(e -> showStatisticsDialog());

		manageButtonsLayout.getChildren().addAll(managePhoneButton, manageEmailButton, statisticsButton);

		HBox buttonLayout = new HBox(10);
		buttonLayout.setAlignment(Pos.CENTER);
		buttonLayout.setPadding(new Insets(10));

		Button addButton = new Button("Add Supplier");
		Button deleteButton = new Button("Delete Supplier");
		Button searchButton = new Button("Search for Supplier");
		Button refreshButton = new Button("Refresh");
		Button homeButton = new Button("Home");

		TextField searchField = new TextField();
		searchField.setPromptText("Enter Supplier ID");

		homeButton.setOnAction(e -> primaryStage.setScene(homeScene));
		addButton.setOnAction(e -> showAddSupplierDialog());
		deleteButton.setOnAction(e -> {
			String idText = searchField.getText();
			if (!idText.isEmpty()) {
				int id = Integer.parseInt(idText);
				deleteSupplier(id);
				readSublayer();
			}
		});
		managePhoneButton.setOnAction(e -> {
			String supplierIdText = searchField.getText();
			if (!supplierIdText.isEmpty() && isNumeric(supplierIdText)) {
				int id = Integer.parseInt(supplierIdText);
				showPhoneNumbersDialog(id);
			} else {
				showAlert(Alert.AlertType.WARNING, "Input Error", "Please enter a valid Supplier ID to manage phones.");
			}
		});

		manageEmailButton.setOnAction(e -> {
			String supplierIdText = searchField.getText();
			if (!supplierIdText.isEmpty() && isNumeric(supplierIdText)) {
				int id = Integer.parseInt(supplierIdText);
				showEmailManagementDialog(id);
			} else {
				showAlert(Alert.AlertType.WARNING, "Input Error", "Please enter a valid Supplier ID to manage emails.");
			}
		});

		searchButton.setOnAction(e -> {
			String idText = searchField.getText();
			if (!idText.isEmpty()) {
				try {
					int id = Integer.parseInt(idText);
					searchSupplier(id);
				} catch (NumberFormatException ex) {
					showAlert(Alert.AlertType.WARNING, "Input Error", "Please enter a valid numeric ID.");
				}
			} else {
				showAlert(Alert.AlertType.WARNING, "Input Error", "Please enter an ID to search.");
			}
		});
		refreshButton.setOnAction(e -> showSearchAndEditDialog());

		buttonLayout.getChildren().addAll(homeButton, searchField, addButton, deleteButton, searchButton,
				refreshButton);

		VBox mainLayout = new VBox(10);
		mainLayout.getChildren().addAll(titleBox, supplierTableView, manageButtonsLayout, buttonLayout);
		mainLayout.setPadding(new Insets(10));
		mainLayout.setStyle("-fx-background-color: #15919B;");

		supplierRoot.setCenter(mainLayout);

		return supplierRoot;
	}

	private void showStatisticsDialog() {
		Stage statisticsStage = new Stage();
		statisticsStage.getIcons().add(new Image(getClass().getResourceAsStream("/statistic.png")));

		statisticsStage.setTitle("Supplier Statistics");

		TabPane tabPane = new TabPane();

		Tab totalSuppliersTab = new Tab("Total Suppliers");
		VBox totalSuppliersLayout = new VBox(10);

		Label totalSuppliersLabel = new Label("Total Suppliers: " + getTotalSuppliers());
		totalSuppliersLabel.getStyleClass().add("label-stat");

		TableView<Supplier> suppliersTable = new TableView<>();
		suppliersTable.setPrefSize(800, 400);

		TableColumn<Supplier, Integer> idCol = new TableColumn<>("ID");
		idCol.setCellValueFactory(new PropertyValueFactory<>("s_id"));

		TableColumn<Supplier, String> nameCol = new TableColumn<>("Name");
		nameCol.setCellValueFactory(new PropertyValueFactory<>("s_name"));

		TableColumn<Supplier, String> emailCol = new TableColumn<>("Email");
		emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

		TableColumn<Supplier, String> phoneCol = new TableColumn<>("Phone Number");
		phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone_number"));

		TableColumn<Supplier, Integer> employeeSsnCol = new TableColumn<>("Employee SSN");
		employeeSsnCol.setCellValueFactory(new PropertyValueFactory<>("employee_ssn"));

		double columnWidth = 800 / 5.0;
		idCol.setPrefWidth(columnWidth);
		nameCol.setPrefWidth(columnWidth);
		emailCol.setPrefWidth(columnWidth);
		phoneCol.setPrefWidth(columnWidth);
		employeeSsnCol.setPrefWidth(columnWidth);

		suppliersTable.getColumns().addAll(idCol, nameCol, emailCol, phoneCol, employeeSsnCol);

		suppliersTable.setItems(getSuppliersData());

		totalSuppliersLayout.getChildren().addAll(totalSuppliersLabel, suppliersTable);
		totalSuppliersTab.setContent(totalSuppliersLayout);

		Tab emailStatsTab = createStatsTab("Emails Per Supplier", getEmailStats());
		Tab phoneStatsTab = createStatsTab("Phones Per Supplier", getPhoneStats());
		Tab employeeStatsTab = createStatsTab("Suppliers by Employee", getEmployeeStats());
		Tab mostPhonesTab = createStatsTab("Most Phone Numbers", getMostPhones());

		tabPane.getTabs().addAll(totalSuppliersTab, emailStatsTab, phoneStatsTab, employeeStatsTab, mostPhonesTab);

		Scene scene = new Scene(tabPane, 800, 600);
		scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
		statisticsStage.setScene(scene);
		statisticsStage.show();
	}

	private Tab createStatsTab(String title, ObservableList<StatEntry> stats) {
		Tab tab = new Tab(title);
		VBox layout = new VBox(10);
		Label label = new Label(title + ":");
		label.getStyleClass().add("label-stat");

		TableView<StatEntry> table = createStatsTable(stats);
		layout.getChildren().addAll(label, table);
		tab.setContent(layout);
		return tab;
	}

	private ObservableList<Supplier> getSuppliersData() {
		ObservableList<Supplier> suppliers = FXCollections.observableArrayList();
		try (Connection con = db.getConnection().connectDB()) {
			String sql = "SELECT s_id, s_name, email, phone_number, employee_ssn FROM supplier";
			PreparedStatement pstmt = con.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				suppliers.add(new Supplier(rs.getInt("s_id"), rs.getString("s_name"), rs.getString("email"),
						rs.getString("phone_number"), rs.getInt("employee_ssn")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return suppliers;
	}

	private int getTotalSuppliers() {
		try (Connection con = db.getConnection().connectDB()) {
			String sql = "SELECT COUNT(*) AS total_suppliers FROM supplier";
			PreparedStatement pstmt = con.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("total_suppliers");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	private ObservableList<StatEntry> getEmailStats() {
		ObservableList<StatEntry> emailStats = FXCollections.observableArrayList();
		try (Connection con = db.getConnection().connectDB()) {
			String sql = "SELECT s.s_id, s.s_name, " + "COUNT(DISTINCT se.email) + "
					+ "CASE WHEN s.email IS NOT NULL THEN 1 ELSE 0 END AS total_emails " + "FROM supplier s "
					+ "LEFT JOIN supplier_email se ON s.s_id = se.supplier_id " + "GROUP BY s.s_id, s.s_name, s.email";
			PreparedStatement pstmt = con.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				emailStats.add(new StatEntry(rs.getInt("s_id"), rs.getString("s_name"), rs.getInt("total_emails")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return emailStats;
	}

	private ObservableList<StatEntry> getPhoneStats() {
		ObservableList<StatEntry> phoneStats = FXCollections.observableArrayList();
		try (Connection con = db.getConnection().connectDB()) {
			String sql = "SELECT s.s_id, s.s_name, " + "COUNT(sp.phone_number) + "
					+ "CASE WHEN s.phone_number IS NOT NULL THEN 1 ELSE 0 END AS total_phones " + "FROM supplier s "
					+ "LEFT JOIN supplier_phone sp ON s.s_id = sp.s_id " + "GROUP BY s.s_id, s.s_name, s.phone_number";

			PreparedStatement pstmt = con.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				phoneStats.add(new StatEntry(rs.getInt("s_id"), rs.getString("s_name"), rs.getInt("total_phones")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return phoneStats;
	}

	private ObservableList<StatEntry> getEmployeeStats() {
		ObservableList<StatEntry> employeeStats = FXCollections.observableArrayList();
		try (Connection con = db.getConnection().connectDB()) {
			String sql = "SELECT employee_ssn, COUNT(*) AS total_suppliers " + "FROM supplier "
					+ "GROUP BY employee_ssn";
			PreparedStatement pstmt = con.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				employeeStats.add(new StatEntry(rs.getInt("employee_ssn"), "Employee", rs.getInt("total_suppliers")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return employeeStats;
	}

	private ObservableList<StatEntry> getMostPhones() {
		ObservableList<StatEntry> mostPhones = FXCollections.observableArrayList();
		try (Connection con = db.getConnection().connectDB()) {
			String sql = "SELECT s.s_id, s.s_name, COUNT(sp.phone_number) AS total_phones " + "FROM supplier s "
					+ "LEFT JOIN supplier_phone sp ON s.s_id = sp.s_id " + "GROUP BY s.s_id, s.s_name "
					+ "ORDER BY total_phones DESC " + "LIMIT 1";
			PreparedStatement pstmt = con.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				mostPhones.add(new StatEntry(rs.getInt("s_id"), rs.getString("s_name"), rs.getInt("total_phones")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mostPhones;
	}

	private TableView<StatEntry> createStatsTable(ObservableList<StatEntry> stats) {
		TableView<StatEntry> table = new TableView<>(stats);

		table.setPrefSize(800, 300);

		TableColumn<StatEntry, Integer> idCol = new TableColumn<>("ID");
		idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

		TableColumn<StatEntry, String> nameCol = new TableColumn<>("Name");
		nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

		TableColumn<StatEntry, Integer> valueCol = new TableColumn<>("Value");
		valueCol.setCellValueFactory(new PropertyValueFactory<>("value"));

		idCol.setPrefWidth(266.66);
		nameCol.setPrefWidth(266.66);
		valueCol.setPrefWidth(266.66);

		table.getColumns().addAll(idCol, nameCol, valueCol);

		table.setStyle("-fx-font-size: 12px; -fx-border-color: white; -fx-border-radius: 5; -fx-padding: 5;");

		return table;
	}

	private void showEmailManagementDialog(int s_id) {
		Stage emailStage = new Stage();
		emailStage.getIcons().add(new Image(getClass().getResourceAsStream("/email.png")));
		emailStage.setTitle("Manage Emails for Supplier ID: " + s_id);

		ObservableList<Email> emailList = FXCollections.observableArrayList();

		String supplierName = getSupplierNameById(s_id);
		if (supplierName == null || supplierName.isEmpty()) {
			showAlert(Alert.AlertType.WARNING, "Invalid Supplier ID",
					"The provided Supplier ID does not exist in the database.");
			return;
		}

		TableView<Email> emailTable = new TableView<>();

		TableColumn<Email, Integer> idColumn = new TableColumn<>("ID");
		idColumn.setCellValueFactory(new PropertyValueFactory<>("s_id"));

		TableColumn<Email, String> nameColumn = new TableColumn<>("Name");
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("supplierName"));

		TableColumn<Email, String> emailColumn = new TableColumn<>("Email");
		emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

		emailTable.getColumns().addAll(idColumn, nameColumn, emailColumn);
		emailTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		idColumn.prefWidthProperty().bind(emailTable.widthProperty().multiply(0.2));
		nameColumn.prefWidthProperty().bind(emailTable.widthProperty().multiply(0.4));
		emailColumn.prefWidthProperty().bind(emailTable.widthProperty().multiply(0.4));

		emailTable.setItems(emailList);

		TextField emailField = new TextField();
		emailField.setPromptText("Enter Email Address");
		emailField.setPrefWidth(250);

		Button addEmailButton = new Button("Add Email");
		addEmailButton.setOnAction(e -> {
			String email = emailField.getText();

			if (email != null && isValidEmail(email)) {
				boolean emailExistsInTable = emailList.stream()
						.anyMatch(em -> em.getEmail().equals(email) && em.getS_id() == s_id);

				boolean emailExistsInDatabase = isEmailExistsForSupplier(s_id, email);

				if (emailExistsInTable || emailExistsInDatabase) {
					showAlert(Alert.AlertType.WARNING, "Duplicate Email",
							"This email address is already assigned to this supplier.");
				} else {
					try (Connection con = db.getConnection().connectDB()) {
						String sql = "INSERT INTO supplier_email (s_id, email) VALUES (?, ?)";
						PreparedStatement pstmt = con.prepareStatement(sql);
						pstmt.setInt(1, s_id);
						pstmt.setString(2, email);
						pstmt.executeUpdate();
						emailList.add(new Email(s_id, supplierName, email));
						emailField.clear();
					} catch (Exception ex) {
						ex.printStackTrace();
						showAlert(Alert.AlertType.ERROR, "Error", "Failed to add email address.");
					}
				}
			} else {
				showAlert(Alert.AlertType.WARNING, "Invalid Input", "Please enter a valid email address.");
			}
		});

		Button deleteEmailButton = new Button("Delete Email");
		deleteEmailButton.setOnAction(e -> {
			Email selectedEmail = emailTable.getSelectionModel().getSelectedItem();
			if (selectedEmail != null) {
				try (Connection con = db.getConnection().connectDB()) {
					String sql = "DELETE FROM supplier_email WHERE email = ? AND s_id = ?";
					PreparedStatement pstmt = con.prepareStatement(sql);
					pstmt.setString(1, selectedEmail.getEmail());
					pstmt.setInt(2, s_id);
					pstmt.executeUpdate();
					emailList.remove(selectedEmail);
					showAlert(Alert.AlertType.INFORMATION, "Success", "Email address deleted successfully.");
				} catch (Exception ex) {
					ex.printStackTrace();
					showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete email address.");
				}
			} else {
				showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an email address to delete.");
			}
		});

		HBox inputLayout = new HBox(10, emailField, addEmailButton, deleteEmailButton);
		inputLayout.setAlignment(Pos.CENTER);

		VBox layout = new VBox(10, emailTable, inputLayout);
		layout.setPadding(new Insets(20));

		Scene scene = new Scene(layout, 600, 400);
		scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
		emailStage.setScene(scene);
		emailStage.show();

		loadEmailsForSupplier(s_id, emailList);
	}

	private void loadEmailsForSupplier(int ssn, ObservableList<Email> emailList) {
		emailList.clear();
		try (Connection con = db.getConnection().connectDB()) {
			String sql = "SELECT s.s_id, s.s_name, s.email " + "FROM supplier s " + "WHERE s.employee_ssn = ?";
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, ssn);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				emailList.add(new Email(rs.getInt("s_id"), rs.getString("s_name"), rs.getString("email")));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Error", "Failed to load email addresses from main table.");
		}
	}

	private boolean isEmailExistsForSupplier(int s_id, String email) {
		try (Connection con = db.getConnection().connectDB()) {
			String sql = "SELECT COUNT(*) FROM supplier_email WHERE s_id = ? AND email = ?";
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, s_id);
			pstmt.setString(2, email);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next() && rs.getInt(1) > 0) {
				return true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	private void showPhoneNumbersDialog(int s_id) {
		Stage phoneStage = new Stage();
		phoneStage.getIcons().add(new Image(getClass().getResourceAsStream("/phone.png")));
		phoneStage.setTitle("Manage Phone Numbers for Supplier ID: " + s_id);

		ObservableList<PhoneEntry> phoneList = FXCollections.observableArrayList();

		String supplierName = getSupplierNameById(s_id);
		if (supplierName == null || supplierName.isEmpty()) {
			showAlert(Alert.AlertType.WARNING, "Invalid Supplier ID",
					"The provided Supplier ID does not exist in the database.");
			return;
		}

		TableView<PhoneEntry> phoneTable = new TableView<>();

		TableColumn<PhoneEntry, Integer> idColumn = new TableColumn<>("ID");
		idColumn.setCellValueFactory(new PropertyValueFactory<>("s_id"));

		TableColumn<PhoneEntry, String> nameColumn = new TableColumn<>("Name");
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("supplierName"));

		TableColumn<PhoneEntry, String> phoneColumn = new TableColumn<>("Phone");
		phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));

		TableColumn<PhoneEntry, String> typeColumn = new TableColumn<>("Type");
		typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

		phoneTable.getColumns().addAll(idColumn, nameColumn, phoneColumn, typeColumn);
		phoneTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		idColumn.prefWidthProperty().bind(phoneTable.widthProperty().multiply(0.25));
		nameColumn.prefWidthProperty().bind(phoneTable.widthProperty().multiply(0.25));
		phoneColumn.prefWidthProperty().bind(phoneTable.widthProperty().multiply(0.25));
		typeColumn.prefWidthProperty().bind(phoneTable.widthProperty().multiply(0.25));

		phoneTable.setItems(phoneList);

		TextField phoneField = new TextField();
		phoneField.setPromptText("Enter Phone Number");
		phoneField.setPrefWidth(150);

		ComboBox<String> typeComboBox = new ComboBox<>();
		typeComboBox.getItems().addAll("Mobile", "Work", "Home");
		typeComboBox.setPromptText("Select Type");
		typeComboBox.setPrefWidth(100);

		Button addPhoneButton = new Button("Add Phone");
		addPhoneButton.setOnAction(e -> {
			String phone = phoneField.getText();
			String type = typeComboBox.getValue();

			if (phone != null && phone.matches("\\d{10}") && type != null) {
				boolean phoneExistsInTable = phoneList.stream()
						.anyMatch(p -> p.getPhone().equals(phone) && p.getS_id() == s_id);

				boolean phoneExistsInDatabase = isPhoneExistsForSupplier(s_id, phone);

				if (phoneExistsInTable || phoneExistsInDatabase) {
					showAlert(Alert.AlertType.WARNING, "Duplicate Phone",
							"This phone number is already assigned to this supplier.");
				} else {
					try (Connection con = db.getConnection().connectDB()) {
						String sql = "INSERT INTO supplier_phone (s_id, phone_number, phone_type) VALUES (?, ?, ?)";
						PreparedStatement pstmt = con.prepareStatement(sql);
						pstmt.setInt(1, s_id);
						pstmt.setString(2, phone);
						pstmt.setString(3, type);
						pstmt.executeUpdate();
						phoneList.add(new PhoneEntry(s_id, supplierName, phone, type));
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
			PhoneEntry selectedPhone = phoneTable.getSelectionModel().getSelectedItem();
			if (selectedPhone != null) {
				try (Connection con = db.getConnection().connectDB()) {
					String sql = "DELETE FROM supplier_phone WHERE phone_number = ? AND s_id = ?";
					PreparedStatement pstmt = con.prepareStatement(sql);
					pstmt.setString(1, selectedPhone.getPhone());
					pstmt.setInt(2, s_id);
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

		loadPhoneNumbersForSupplier(s_id, supplierName, phoneList);
	}

	private void loadPhoneNumbersForSupplier(int s_id, String supplierName, ObservableList<PhoneEntry> phoneList) {
		phoneList.clear();
		try (Connection con = db.getConnection().connectDB()) {
			String sql = "SELECT phone_number FROM supplier WHERE s_id = ?";
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, s_id);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				phoneList.add(new PhoneEntry(s_id, supplierName, rs.getString("phone_number"), "General")); // استبدال
																											// type بـ
																											// "Unknown"
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Error", "Failed to load phone numbers from the main table.");
		}
	}

	private String getSupplierNameById(int s_id) {
		String name = "";
		try (Connection con = db.getConnection().connectDB()) {
			String sql = "SELECT s_name FROM supplier WHERE s_id = ?";
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, s_id);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				name = rs.getString("s_name");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Error", "Failed to retrieve supplier name.");
		}
		return name;
	}

	private boolean isPhoneExistsForSupplier(int s_id, String phone) {
		try (Connection con = db.getConnection().connectDB()) {
			String sql = "SELECT COUNT(*) FROM supplier_phone WHERE s_id = ? AND phone_number = ?";
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, s_id);
			pstmt.setString(2, phone);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next() && rs.getInt(1) > 0) {
				return true;
			}
			rs.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	private void showSearchAndEditDialog() {
		Stage searchEditStage = new Stage();
		VBox searchEditLayout = new VBox(10);
		searchEditLayout.setPadding(new Insets(20));

		searchEditStage.setTitle("Search and Edit Supplier");
		searchEditStage.getIcons().add(new Image(getClass().getResourceAsStream("/88.png")));

		TextField idField = new TextField();
		idField.setPromptText("Enter Supplier ID");

		Button searchButton = new Button("Search");
		searchButton.getStyleClass().add("action-button");

		TextField nameField = new TextField();
		nameField.setPromptText("Name");
		nameField.setDisable(true);

		TextField emailField = new TextField();
		emailField.setPromptText("Email");
		emailField.setDisable(true);

		TextField phoneNumberField = new TextField();
		phoneNumberField.setPromptText("Phone Number");
		phoneNumberField.setDisable(true);

		TextField employeeSSNField = new TextField();
		employeeSSNField.setPromptText("Employee SSN");
		employeeSSNField.setDisable(true);

		Button saveButton = new Button("Save");
		saveButton.getStyleClass().add("action-button");
		saveButton.setDisable(true);

		final Supplier[] currentSupplier = new Supplier[1];

		searchButton.setOnAction(e -> {
			try {
				int id = Integer.parseInt(idField.getText());
				Connection con = db.getConnection().connectDB();
				String sql = "SELECT * FROM supplier WHERE s_id = ?";
				PreparedStatement pstmt = con.prepareStatement(sql);
				pstmt.setInt(1, id);
				ResultSet rs = pstmt.executeQuery();

				if (rs.next()) {
					nameField.setText(rs.getString("s_name"));
					emailField.setText(rs.getString("email"));
					phoneNumberField.setText(rs.getString("phone_number"));
					employeeSSNField.setText(String.valueOf(rs.getInt("employee_ssn")));

					nameField.setDisable(false);
					emailField.setDisable(false);
					phoneNumberField.setDisable(false);
					employeeSSNField.setDisable(false);
					saveButton.setDisable(false);

					currentSupplier[0] = supplierList.stream().filter(s -> s.getS_id() == id).findFirst().orElse(null);
				} else {
					showAlert(Alert.AlertType.WARNING, "Not Found", "Supplier with ID " + id + " not found.");
				}

				rs.close();
				pstmt.close();
				con.close();
			} catch (Exception ex) {
				ex.printStackTrace();
				showAlert(Alert.AlertType.ERROR, "Error", "Failed to retrieve Supplier data.");
			}
		});

		saveButton.setOnAction(e -> {
			try {
				if (idField.getText().isEmpty() || !isNumeric(idField.getText())) {
					showAlert(Alert.AlertType.ERROR, "Invalid Input",
							"ID must be a numeric value and cannot be empty.");
					return;
				}

				int id = Integer.parseInt(idField.getText());
				String name = nameField.getText();
				String email = emailField.getText();
				String phoneNumber = phoneNumberField.getText();

				if (employeeSSNField.getText().isEmpty() || !isNumeric(employeeSSNField.getText())) {
					showAlert(Alert.AlertType.ERROR, "Invalid Input",
							"Employee SSN must be a numeric value and cannot be empty.");
					return;
				}

				int employeeSSN = Integer.parseInt(employeeSSNField.getText());

				if (name.isEmpty()) {
					showAlert(Alert.AlertType.ERROR, "Invalid Input", "Name cannot be empty.");
					return;
				}

				if (!isValidEmail(email)) {
					showAlert(Alert.AlertType.ERROR, "Invalid Email", "Please enter a valid email address.");
					return;
				}

				if (!isValidPhoneNumber(phoneNumber)) {
					showAlert(Alert.AlertType.ERROR, "Invalid Phone Number", "Phone Number must be exactly 10 digits.");
					return;
				}

				Connection con = db.getConnection().connectDB();

				String checkEmployeeSQL = "SELECT type FROM employee WHERE snn = ?";
				PreparedStatement checkEmployeeStmt = con.prepareStatement(checkEmployeeSQL);
				checkEmployeeStmt.setInt(1, employeeSSN);
				ResultSet rs = checkEmployeeStmt.executeQuery();

				if (!rs.next()) {
					showAlert(Alert.AlertType.ERROR, "Invalid Employee SSN",
							"Employee SSN does not exist in the database.");
					rs.close();
					checkEmployeeStmt.close();
					con.close();
					return;
				}

				String employeeType = rs.getString("type");
				if (!"Manager".equalsIgnoreCase(employeeType)) {
					showAlert(Alert.AlertType.ERROR, "Invalid Employee", "The Employee is not a Manager.");
					rs.close();
					checkEmployeeStmt.close();
					con.close();
					return;
				}

				rs.close();
				checkEmployeeStmt.close();

				String sql = "UPDATE supplier SET s_name = ?, email = ?, phone_number = ?, employee_ssn = ? WHERE s_id = ?";
				PreparedStatement pstmt = con.prepareStatement(sql);
				pstmt.setString(1, name);
				pstmt.setString(2, email);
				pstmt.setString(3, phoneNumber);
				pstmt.setInt(4, employeeSSN);
				pstmt.setInt(5, id);

				int rowsAffected = pstmt.executeUpdate();
				pstmt.close();
				con.close();

				if (rowsAffected > 0) {
					if (currentSupplier[0] != null) {
						currentSupplier[0].setS_name(name);
						currentSupplier[0].setEmail(email);
						currentSupplier[0].setPhone_number(phoneNumber);
						currentSupplier[0].setEmployee_ssn(employeeSSN);

						supplierTableView.refresh();
					}

					showAlert(Alert.AlertType.INFORMATION, "Success", "Supplier details updated successfully.");
					searchEditStage.close();
				} else {
					showAlert(Alert.AlertType.WARNING, "Not Found", "No Supplier found with the provided ID.");
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
				showAlert(Alert.AlertType.ERROR, "Database Error",
						"Failed to update Supplier details. Please check the database.");
			} catch (Exception ex) {
				ex.printStackTrace();
				showAlert(Alert.AlertType.ERROR, "Error",
						"An unexpected error occurred while updating Supplier details.");
			}
		});

		searchEditLayout.getChildren().addAll(idField, searchButton, nameField, emailField, phoneNumberField,
				employeeSSNField, saveButton);

		Scene searchEditScene = new Scene(searchEditLayout, 400, 300);
		searchEditScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
		searchEditStage.setScene(searchEditScene);
		searchEditStage.show();
	}

	private void deleteSupplier(int id) {
		try {
			Connection con = db.getConnection().connectDB();
			String sql = "DELETE FROM supplier WHERE s_id=?";
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, id);

			int affectedRows = pstmt.executeUpdate();

			pstmt.close();
			con.close();

			if (affectedRows > 0) {
				showAlert(Alert.AlertType.INFORMATION, "Success",
						"Supplier with ID " + id + " was successfully deleted.");
			} else {
				showAlert(Alert.AlertType.WARNING, "Not Found",
						"Supplier with ID " + id + " was not found in the database.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Error",
					"An error occurred while trying to delete the Supplier. Please try again.");
		}
	}

	private void searchSupplier(int id) {
		try {
			Connection con = db.getConnection().connectDB();
			String sql = "SELECT * FROM supplier WHERE s_id=?";
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, id);
			ResultSet rs = pstmt.executeQuery();

			ObservableList<Supplier> tempSupplierList = FXCollections.observableArrayList();

			if (rs.next()) {
				int s_id = rs.getInt("s_id");
				String s_name = rs.getString("s_name");
				String email = rs.getString("email");
				String phoneNumber = rs.getString("phone_number");
				int employeeSSN = rs.getInt("employee_ssn");

				tempSupplierList.add(new Supplier(s_id, s_name, email, phoneNumber, employeeSSN));

				Stage resultStage = new Stage();
				resultStage.setTitle("Supplier Details");
				resultStage.getIcons().add(new Image(getClass().getResourceAsStream("/99.png")));

				TableView<Supplier> resultTableView = new TableView<>();
				resultTableView.setItems(tempSupplierList);

				TableColumn<Supplier, Integer> s_idCol = new TableColumn<>("ID");
				s_idCol.setCellValueFactory(new PropertyValueFactory<>("s_id"));

				TableColumn<Supplier, String> s_nameCol = new TableColumn<>("Name");
				s_nameCol.setCellValueFactory(new PropertyValueFactory<>("s_name"));

				TableColumn<Supplier, String> emailCol = new TableColumn<>("Email");
				emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

				TableColumn<Supplier, String> phoneCol = new TableColumn<>("Phone Number");
				phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone_number"));

				TableColumn<Supplier, Integer> employeeSSNCol = new TableColumn<>("Employee SSN");
				employeeSSNCol.setCellValueFactory(new PropertyValueFactory<>("employee_ssn"));

				resultTableView.getColumns().addAll(s_idCol, s_nameCol, emailCol, phoneCol, employeeSSNCol);
				resultTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

				VBox layout = new VBox(10, resultTableView);
				layout.setPadding(new Insets(20));

				Scene resultScene = new Scene(layout, 600, 400);
				resultScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
				resultStage.setScene(resultScene);

				resultStage.show();

			} else {
				showAlert(Alert.AlertType.WARNING, "Supplier Not Found",
						"The sublayer with ID " + id + " was not found.");
			}

			rs.close();
			pstmt.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while searching for the Supplier.");
		}
	}

	private void showAddSupplierDialog() {
		Stage addSublayerStage = new Stage();
		addSublayerStage.getIcons().add(new Image(getClass().getResourceAsStream("/people.png")));
		VBox addSublayerLayout = new VBox(15);
		addSublayerLayout.setPadding(new Insets(20));
		addSublayerLayout.setAlignment(Pos.CENTER);

		TextField s_idField = new TextField();
		s_idField.setPromptText("ID");
		s_idField.getStyleClass().add("input-field");

		TextField s_nameField = new TextField();
		s_nameField.setPromptText("Name");
		s_nameField.getStyleClass().add("input-field");

		TextField emailField = new TextField();
		emailField.setPromptText("Email");
		emailField.getStyleClass().add("input-field");

		TextField phoneNumberField = new TextField();
		phoneNumberField.setPromptText("Phone Number");
		phoneNumberField.getStyleClass().add("input-field");

		TextField employeeSSNField = new TextField();
		employeeSSNField.setPromptText("Employee SSN");
		employeeSSNField.getStyleClass().add("input-field");

		Button addBtn = new Button("Add");
		addBtn.getStyleClass().add("action-button");

		addBtn.setOnAction(e -> {
			try {
				if (!isNumeric(s_idField.getText())) {
					showAlert(Alert.AlertType.ERROR, "Invalid Input", "ID must be a numeric value.");
					return;
				}

				if (s_nameField.getText().isEmpty()) {
					showAlert(Alert.AlertType.ERROR, "Invalid Input", "Name cannot be empty.");
					return;
				}

				if (!isValidEmail(emailField.getText())) {
					showAlert(Alert.AlertType.ERROR, "Invalid Input",
							"Invalid email format. Please enter a valid email.");
					return;
				}

				if (!isValidPhoneNumber(phoneNumberField.getText())) {
					showAlert(Alert.AlertType.ERROR, "Invalid Input", "Phone Number must be exactly 10 digits.");
					return;
				}

				if (!isNumeric(employeeSSNField.getText())) {
					showAlert(Alert.AlertType.ERROR, "Invalid Input", "Employee SSN must be a numeric value.");
					return;
				}

				int employeeSSN = Integer.parseInt(employeeSSNField.getText());
				Connection con = db.getConnection().connectDB();

				String checkEmployeeSQL = "SELECT type FROM employee WHERE snn = ?";
				PreparedStatement checkEmployeeStmt = con.prepareStatement(checkEmployeeSQL);
				checkEmployeeStmt.setInt(1, employeeSSN);
				ResultSet employeeRs = checkEmployeeStmt.executeQuery();

				if (!employeeRs.next()) {
					showAlert(Alert.AlertType.ERROR, "Invalid Employee",
							"The Employee SSN does not exist in the database.");
					employeeRs.close();
					checkEmployeeStmt.close();
					con.close();
					return;
				}

				String employeeType = employeeRs.getString("type");
				if (!"Manager".equalsIgnoreCase(employeeType)) {
					showAlert(Alert.AlertType.ERROR, "Invalid Employee", "The Employee is not a Manager.");
					employeeRs.close();
					checkEmployeeStmt.close();
					con.close();
					return;
				}

				employeeRs.close();
				checkEmployeeStmt.close();

				int s_id = Integer.parseInt(s_idField.getText());
				String checkSublayerSQL = "SELECT s_id FROM supplier WHERE s_id = ?";
				PreparedStatement checkSublayerStmt = con.prepareStatement(checkSublayerSQL);
				checkSublayerStmt.setInt(1, s_id);
				ResultSet sublayerRs = checkSublayerStmt.executeQuery();

				if (sublayerRs.next()) {
					showAlert(Alert.AlertType.ERROR, "Duplicate Entry", "The ID already exists in the sublayer table.");
					sublayerRs.close();
					checkSublayerStmt.close();
					con.close();
					return;
				}
				sublayerRs.close();
				checkSublayerStmt.close();

				String sql = "INSERT INTO supplier (s_id, s_name, email, phone_number, employee_ssn) VALUES (?, ?, ?, ?, ?)";
				PreparedStatement pstmt = con.prepareStatement(sql);
				pstmt.setInt(1, s_id);
				pstmt.setString(2, s_nameField.getText());
				pstmt.setString(3, emailField.getText());
				pstmt.setString(4, phoneNumberField.getText());
				pstmt.setInt(5, employeeSSN);
				pstmt.executeUpdate();

				supplierList.add(new Supplier(s_id, s_nameField.getText(), emailField.getText(),
						phoneNumberField.getText(), employeeSSN));
				con.close();

				readSublayer();
				addSublayerStage.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
				showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add supplier. Please check your inputs.");
			} catch (Exception ex) {
				ex.printStackTrace();
				showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred.");
			}
		});

		addSublayerLayout.getChildren().addAll(s_idField, s_nameField, emailField, phoneNumberField, employeeSSNField,
				addBtn);

		Scene addSublayerScene = new Scene(addSublayerLayout, 400, 300);
		addSublayerScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
		addSublayerStage.setScene(addSublayerScene);
		addSublayerStage.setTitle("Add Supplier");
		addSublayerStage.show();
	}

	private boolean isNumeric(String text) {
		return text != null && text.matches("\\d+");
	}

	private boolean isValidEmail(String email) {
		String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
		return email != null && email.matches(emailRegex);
	}

	private boolean isValidPhoneNumber(String phoneNumber) {
		return phoneNumber != null && phoneNumber.matches("\\d{10}");
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