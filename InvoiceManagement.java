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
import java.time.LocalDate;

public class InvoiceManagement {
	dataBaseConnection db = new dataBaseConnection();
	ObservableList<Invoice> invoiceList = FXCollections.observableArrayList();
	TableView<Invoice> invoiceTableView = new TableView<>();

	public void readInvoices() {
		invoiceList.clear();
		try {
			Connection con = db.getConnection().connectDB();
			String sql = "SELECT * FROM invoice";
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				invoiceList.add(new Invoice(rs.getInt("invoice_id"), rs.getDouble("amount"), rs.getString("date"),
						rs.getString("status"), rs.getString("due_date"), rs.getInt("cid")));
			}
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		invoiceTableView.setItems(invoiceList);
	}

	public BorderPane createInvoiceManagementLayout(Stage primaryStage, Scene homeScene) {
		BorderPane invoiceRoot = new BorderPane();
		invoiceRoot.setPrefSize(800, 600);
		invoiceRoot.setStyle("-fx-background-color: #15919B;");

		Label titleLabel = new Label("Invoice Management");
		titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: white;");
		VBox titleBox = new VBox(titleLabel);
		titleBox.setAlignment(Pos.CENTER);
		titleBox.setPadding(new Insets(10));

		TableColumn<Invoice, Integer> invoiceIdCol = new TableColumn<>("Invoice ID");
		TableColumn<Invoice, Double> amountCol = new TableColumn<>("Amount");
		TableColumn<Invoice, String> dateCol = new TableColumn<>("Date");
		TableColumn<Invoice, String> statusCol = new TableColumn<>("Status");
		TableColumn<Invoice, String> dueDateCol = new TableColumn<>("Due Date");
		TableColumn<Invoice, Integer> patientIdCol = new TableColumn<>("Patient ID");

		double tableWidth = 800;
		double columnWidth = tableWidth / 6;
		invoiceIdCol.setPrefWidth(columnWidth);
		amountCol.setPrefWidth(columnWidth);
		dateCol.setPrefWidth(columnWidth);
		statusCol.setPrefWidth(columnWidth);
		dueDateCol.setPrefWidth(columnWidth);
		patientIdCol.setPrefWidth(columnWidth);

		invoiceIdCol.setCellValueFactory(new PropertyValueFactory<>("invoiceId"));
		amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
		dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
		statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
		dueDateCol.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
		patientIdCol.setCellValueFactory(new PropertyValueFactory<>("cid"));

		invoiceTableView.getColumns().addAll(invoiceIdCol, amountCol, dateCol, statusCol, dueDateCol, patientIdCol);

		Button homeButton = new Button("Home");
		homeButton.setOnAction(e -> primaryStage.setScene(homeScene));

		TextField searchField = new TextField();
		searchField.setPromptText("Enter Invoice ID");

		Button addButton = new Button("Add");
		Button deleteButton = new Button("Delete");
		Button searchButton = new Button("Search");
		Button refreshButton = new Button("Refresh");
		Button showStatsButton = new Button("Show Statistics");

		HBox buttonLayout = new HBox(10, homeButton, searchField, addButton, deleteButton,
				searchButton, refreshButton,showStatsButton);
		buttonLayout.setAlignment(Pos.CENTER);
		buttonLayout.setPadding(new Insets(10));

		VBox centerLayout = new VBox(10, invoiceTableView, buttonLayout);
		centerLayout.setAlignment(Pos.CENTER);
		centerLayout.setPadding(new Insets(10));

		invoiceRoot.setTop(titleBox);
		invoiceRoot.setCenter(centerLayout);
		deleteButton.setOnAction(e -> {
			String invoiceIdText = searchField.getText();
			if (!invoiceIdText.isEmpty()) {
				int invoiceId = Integer.parseInt(invoiceIdText);
				deleteInvoice(invoiceId);
				readInvoices();
			} else {
				showAlert(Alert.AlertType.WARNING, "Input Error", "Please enter an Invoice ID to delete.");
			}
		});

		showStatsButton.setOnAction(e -> showStatistics());
		searchButton.setOnAction(e -> {
			String invoiceIdText = searchField.getText();
			if (invoiceIdText.isEmpty()) {
				showAlert(Alert.AlertType.WARNING, "Input Error", "Please enter an Invoice ID to search.");
				return;
			}

			try {
				int invoiceId = Integer.parseInt(invoiceIdText);
				searchInvoice(invoiceId);
			} catch (NumberFormatException ex) {
				showAlert(Alert.AlertType.WARNING, "Input Error", "Invoice ID must be a number.");
			}
		});
		refreshButton.setOnAction(e -> refreshInvoice());

		addButton.setOnAction(e -> addInvoice());

		return invoiceRoot;
	}

	private void showStatistics() {
		double totalAmount = 0.0;
		int paidCount = 0;
		int unpaidCount = 0;

		try {
			Connection con = db.getConnection().connectDB();

			String totalAmountSQL = "SELECT SUM(amount) AS total FROM invoice";
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(totalAmountSQL);
			if (rs.next()) {
				totalAmount = rs.getDouble("total");
			}

			String countStatusSQL = "SELECT status, COUNT(*) AS count FROM invoice GROUP BY status";
			ResultSet statusRS = stmt.executeQuery(countStatusSQL);

			while (statusRS.next()) {
				String status = statusRS.getString("status");
				int count = statusRS.getInt("count");

				if ("Paid".equals(status)) {
					paidCount = count;
				} else if ("Unpaid".equals(status)) {
					unpaidCount = count;
				}
			}

			Stage statsStage = new Stage();
			statsStage.getIcons().add(new Image(getClass().getResourceAsStream("/statistic.png")));

			statsStage.setTitle("Invoice Statistics");

			VBox statsLayout = new VBox(10);
			statsLayout.setPadding(new Insets(20));
			statsLayout.setStyle("-fx-background-color: #15919B; -fx-alignment: top-left;");

			Label statsTitleLabel = new Label("Statistics:");
			statsTitleLabel
					.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white; -fx-padding: 10;");
			statsTitleLabel.setAlignment(Pos.BASELINE_LEFT);

			Label totalAmountLabel = new Label("Total Amount: " + totalAmount);
			totalAmountLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");
			totalAmountLabel.setAlignment(Pos.BASELINE_LEFT);

			Label paidCountLabel = new Label("Paid Invoices: " + paidCount);
			paidCountLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");
			paidCountLabel.setAlignment(Pos.BASELINE_LEFT);

			Label unpaidCountLabel = new Label("Unpaid Invoices: " + unpaidCount);
			unpaidCountLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");
			unpaidCountLabel.setAlignment(Pos.BASELINE_LEFT);

			statsLayout.getChildren().addAll(statsTitleLabel, totalAmountLabel, paidCountLabel, unpaidCountLabel);

			Scene statsScene = new Scene(statsLayout, 300, 200);
			statsStage.setScene(statsScene);
			statsStage.show();

			rs.close();
			statusRS.close();
			stmt.close();
			con.close();

		} catch (SQLException e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Error", "Failed to retrieve statistics.");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void refreshInvoice() {
		Stage refreshStage = new Stage();
		refreshStage.setTitle("Refresh Invoice");
		refreshStage.getIcons().add(new Image(getClass().getResourceAsStream("/88.png")));

		refreshStage.setResizable(false);

		Label idLabel = new Label("Enter Invoice ID:");
		idLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");

		TextField invoiceIdField = new TextField();
		invoiceIdField.setPromptText("Invoice ID");

		Button searchButton = new Button("Search");
		searchButton.setStyle("-fx-background-color: linear-gradient(#C3C7F4, #15919B); " + "-fx-text-fill: white; "
				+ "-fx-font-weight: bold;");

		TextField amountField = new TextField();
		amountField.setPromptText("Amount");
		amountField.setDisable(true);

		DatePicker dateField = new DatePicker();
		dateField.setPromptText("Date");
		dateField.setDisable(true);

		DatePicker dueDateField = new DatePicker();
		dueDateField.setPromptText("Due Date");
		dueDateField.setDisable(true);

		ToggleGroup statusGroup = new ToggleGroup();
		RadioButton paidRadioButton = new RadioButton("Paid");
		paidRadioButton.setToggleGroup(statusGroup);
		paidRadioButton.setDisable(true);
		paidRadioButton.setStyle("-fx-text-fill: white;");

		RadioButton unpaidRadioButton = new RadioButton("Unpaid");
		unpaidRadioButton.setToggleGroup(statusGroup);
		unpaidRadioButton.setDisable(true);
		unpaidRadioButton.setStyle("-fx-text-fill: white;");

		HBox statusLayout = new HBox(10, paidRadioButton, unpaidRadioButton);
		statusLayout.setAlignment(Pos.CENTER);

		TextField patientIdField = new TextField();
		patientIdField.setPromptText("Patient ID");
		patientIdField.setDisable(true);

		Button saveButton = new Button("Save");
		saveButton.setStyle("-fx-background-color: linear-gradient(#C3C7F4, #15919B); " + "-fx-text-fill: white; "
				+ "-fx-font-weight: bold;");
		saveButton.setDisable(true);

		Button cancelButton = new Button("Cancel");
		cancelButton.setStyle("-fx-background-color: linear-gradient(#C3C7F4, #15919B); " + "-fx-text-fill: white; "
				+ "-fx-font-weight: bold;");

		searchButton.setOnAction(e -> {
			try {
				int invoiceId = Integer.parseInt(invoiceIdField.getText());
				Connection con = db.getConnection().connectDB();
				String sql = "SELECT * FROM invoice WHERE invoice_id = ?";
				PreparedStatement pstmt = con.prepareStatement(sql);
				pstmt.setInt(1, invoiceId);
				ResultSet rs = pstmt.executeQuery();

				if (rs.next()) {
					amountField.setText(String.valueOf(rs.getDouble("amount")));
					dateField.setValue(LocalDate.parse(rs.getString("date")));
					dueDateField.setValue(LocalDate.parse(rs.getString("due_date")));
					patientIdField.setText(String.valueOf(rs.getInt("cid")));

					String status = rs.getString("status");
					if (status.equals("Paid")) {
						paidRadioButton.setSelected(true);
					} else {
						unpaidRadioButton.setSelected(true);
					}

					amountField.setDisable(false);
					dateField.setDisable(false);
					dueDateField.setDisable(false);
					patientIdField.setDisable(false);
					paidRadioButton.setDisable(false);
					unpaidRadioButton.setDisable(false);
					saveButton.setDisable(false);
				} else {
					showAlert(Alert.AlertType.WARNING, "Not Found", "No invoice found with the given ID.");
				}

				rs.close();
				pstmt.close();
				con.close();
			} catch (NumberFormatException ex) {
				showAlert(Alert.AlertType.ERROR, "Input Error", "Please enter a valid numeric Invoice ID.");
			} catch (Exception ex) {
				ex.printStackTrace();
				showAlert(Alert.AlertType.ERROR, "Error", "Failed to retrieve invoice data.");
			}
		});

		saveButton.setOnAction(e -> {
			try {
				if (invoiceIdField.getText().isEmpty() || amountField.getText().isEmpty()
						|| dateField.getValue() == null || dueDateField.getValue() == null
						|| patientIdField.getText().isEmpty()) {
					showAlert(Alert.AlertType.ERROR, "Input Error", "All fields must be filled.");
					return;
				}

				int invoiceId;
				try {
					invoiceId = Integer.parseInt(invoiceIdField.getText());
				} catch (NumberFormatException ex) {
					showAlert(Alert.AlertType.ERROR, "Input Error", "Invoice ID must be a numeric value.");
					return;
				}

				double amount;
				try {
					amount = Double.parseDouble(amountField.getText());
				} catch (NumberFormatException ex) {
					showAlert(Alert.AlertType.ERROR, "Input Error", "Amount must be a numeric value.");
					return;
				}

				LocalDate date = dateField.getValue();
				LocalDate dueDate = dueDateField.getValue();
				if (dueDate.isBefore(date)) {
					showAlert(Alert.AlertType.ERROR, "Date Error", "Due Date must be greater than or equal to Date.");
					return;
				}

				int patientId;
				try {
					patientId = Integer.parseInt(patientIdField.getText());
				} catch (NumberFormatException ex) {
					showAlert(Alert.AlertType.ERROR, "Input Error", "Patient ID must be a numeric value.");
					return;
				}

				Connection con = db.getConnection().connectDB();
				String checkPatientSql = "SELECT cid FROM patients WHERE cid = ?";
				PreparedStatement checkPatientStmt = con.prepareStatement(checkPatientSql);
				checkPatientStmt.setInt(1, patientId);
				ResultSet patientRs = checkPatientStmt.executeQuery();

				if (!patientRs.next()) {
					showAlert(Alert.AlertType.ERROR, "Patient Error", "Patient ID does not exist.");
					patientRs.close();
					checkPatientStmt.close();
					con.close();
					return;
				}

				patientRs.close();
				checkPatientStmt.close();

				String status = paidRadioButton.isSelected() ? "Paid" : "Unpaid";

				String sql = "UPDATE invoice SET amount = ?, date = ?, due_date = ?, status = ?, cid = ? WHERE invoice_id = ?";
				PreparedStatement pstmt = con.prepareStatement(sql);
				pstmt.setDouble(1, amount);
				pstmt.setString(2, date.toString());
				pstmt.setString(3, dueDate.toString());
				pstmt.setString(4, status);
				pstmt.setInt(5, patientId);
				pstmt.setInt(6, invoiceId);

				int rowsAffected = pstmt.executeUpdate();
				if (rowsAffected > 0) {
					showAlert(Alert.AlertType.INFORMATION, "Success", "Invoice updated successfully.");
					readInvoices();
					refreshStage.close();
				} else {
					showAlert(Alert.AlertType.WARNING, "Failed", "Failed to update the invoice.");
				}

				pstmt.close();
				con.close();
			} catch (Exception ex) {
				ex.printStackTrace();
				showAlert(Alert.AlertType.ERROR, "Error", "Failed to update invoice.");
			}
		});

		cancelButton.setOnAction(e -> refreshStage.close());

		VBox fieldsLayout = new VBox(10, idLabel, invoiceIdField, searchButton, amountField,
				new HBox(10, dateField, dueDateField), statusLayout, patientIdField,
				new HBox(10, saveButton, cancelButton));
		fieldsLayout.setPadding(new Insets(20));
		fieldsLayout.setAlignment(Pos.CENTER);
		fieldsLayout.setStyle("-fx-background-color: #15919B;");

		Scene scene = new Scene(fieldsLayout, 400, 315);
		scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
		refreshStage.setScene(scene);
		refreshStage.show();
	}

	public void searchInvoice(int invoiceId) {
		Stage searchStage = new Stage();
		searchStage.getIcons().add(new Image(getClass().getResourceAsStream("/99.png")));

		TableView<Invoice> searchResultTable = new TableView<>();
		ObservableList<Invoice> searchResultList = FXCollections.observableArrayList();

		double tableWidth = 800.0;

		TableColumn<Invoice, Integer> invoiceIdCol = new TableColumn<>("Invoice ID");
		invoiceIdCol.setCellValueFactory(new PropertyValueFactory<>("invoiceId"));
		invoiceIdCol.setPrefWidth(tableWidth / 6);

		TableColumn<Invoice, Double> amountCol = new TableColumn<>("Amount");
		amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
		amountCol.setPrefWidth(tableWidth / 6);

		TableColumn<Invoice, String> dateCol = new TableColumn<>("Date");
		dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
		dateCol.setPrefWidth(tableWidth / 6);

		TableColumn<Invoice, String> statusCol = new TableColumn<>("Status");
		statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
		statusCol.setPrefWidth(tableWidth / 6);

		TableColumn<Invoice, String> dueDateCol = new TableColumn<>("Due Date");
		dueDateCol.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
		dueDateCol.setPrefWidth(tableWidth / 6);

		TableColumn<Invoice, Integer> cidCol = new TableColumn<>("Customer ID");
		cidCol.setCellValueFactory(new PropertyValueFactory<>("cid"));
		cidCol.setPrefWidth(tableWidth / 6);

		searchResultTable.getColumns().addAll(invoiceIdCol, amountCol, dateCol, statusCol, dueDateCol, cidCol);
		searchResultTable.setPrefWidth(tableWidth);

		try {
			Connection con = db.getConnection().connectDB();
			String sql = "SELECT * FROM invoice WHERE invoice_id = ?";
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, invoiceId);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				searchResultList.add(new Invoice(rs.getInt("invoice_id"), rs.getDouble("amount"), rs.getString("date"),
						rs.getString("status"), rs.getString("due_date"), rs.getInt("cid")));
			} else {
				showAlert(Alert.AlertType.INFORMATION, "No Results", "No invoice found with the given ID.");
			}
			searchResultTable.setItems(searchResultList);

			rs.close();
			pstmt.close();
			con.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Error", "Failed to search invoice.");
		}

		BorderPane layout = new BorderPane();
		layout.setCenter(searchResultTable);

		Scene scene = new Scene(layout, 840, 400);
		scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
		searchStage.setTitle("Search Results");
		searchStage.setScene(scene);
		searchStage.show();
	}

	private void addInvoice() {
		Stage addInvoiceStage = new Stage();
		addInvoiceStage.setTitle("Add New Invoice");
		addInvoiceStage.getIcons().add(new Image(getClass().getResourceAsStream("/333.png")));

		Label titleLabel = new Label("Add New Invoice");
		titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: white;");
		titleLabel.setAlignment(Pos.CENTER);

		TextField amountField = new TextField();
		amountField.setPromptText("Enter Amount");

		TextField patientIdField = new TextField();
		patientIdField.setPromptText("Enter Patient ID");

		DatePicker dateField = new DatePicker();
		dateField.setPromptText("Select Date");

		DatePicker dueDateField = new DatePicker();
		dueDateField.setPromptText("Select Due Date");

		ToggleGroup statusGroup = new ToggleGroup();
		RadioButton paidRadioButton = new RadioButton("Paid");
		paidRadioButton.setToggleGroup(statusGroup);
		paidRadioButton.setSelected(true);
		paidRadioButton.setStyle("-fx-text-fill: white;");

		RadioButton unpaidRadioButton = new RadioButton("Unpaid");
		unpaidRadioButton.setToggleGroup(statusGroup);
		unpaidRadioButton.setStyle("-fx-text-fill: white;");

		HBox statusLayout = new HBox(10, paidRadioButton, unpaidRadioButton);
		statusLayout.setAlignment(Pos.CENTER);

		Button saveButton = new Button("Save");
		Button cancelButton = new Button("Cancel");

		saveButton.setOnAction(e -> {
			try {
				double amount = Double.parseDouble(amountField.getText());
				String status = paidRadioButton.isSelected() ? "Paid" : "Unpaid";
				int patientId = Integer.parseInt(patientIdField.getText());

				if (dateField.getValue() == null || dueDateField.getValue() == null) {
					showAlert(Alert.AlertType.ERROR, "Input Error", "Please select both Date and Due Date.");
					return;
				}

				String date = dateField.getValue().toString();
				String dueDate = dueDateField.getValue().toString();

				if (dateField.getValue().isAfter(dueDateField.getValue())) {
					showAlert(Alert.AlertType.ERROR, "Input Error", "Due Date must be greater than or equal to Date.");
					return;
				}

				Connection con = db.getConnection().connectDB();
				String checkPatientSql = "SELECT * FROM patients WHERE cid = ?";
				PreparedStatement checkPatientStmt = con.prepareStatement(checkPatientSql);
				checkPatientStmt.setInt(1, patientId);
				ResultSet rs = checkPatientStmt.executeQuery();

				if (!rs.next()) {
					showAlert(Alert.AlertType.ERROR, "Error",
							"Patient ID does not exist in the database. Please add the patient first.");
					con.close();
					return;
				}

				String sql = "INSERT INTO invoice (amount, date, status, due_date, cid) VALUES (?, ?, ?, ?, ?)";
				PreparedStatement pstmt = con.prepareStatement(sql);
				pstmt.setDouble(1, amount);
				pstmt.setString(2, date);
				pstmt.setString(3, status);
				pstmt.setString(4, dueDate);
				pstmt.setInt(5, patientId);

				int rowsAffected = pstmt.executeUpdate();
				if (rowsAffected > 0) {
					showAlert(Alert.AlertType.INFORMATION, "Success", "Invoice added successfully!");
					readInvoices();
					addInvoiceStage.close();
				}
				con.close();
			} catch (NumberFormatException ex) {
				showAlert(Alert.AlertType.ERROR, "Input Error", "Amount must be a valid numeric value.");
			} catch (Exception ex) {
				showAlert(Alert.AlertType.ERROR, "Error", "Failed to add invoice. Please try again.");
				ex.printStackTrace();
			}
		});

		cancelButton.setOnAction(e -> addInvoiceStage.close());

		HBox buttonLayout = new HBox(10, saveButton, cancelButton);
		buttonLayout.setAlignment(Pos.CENTER);

		VBox fieldsLayout = new VBox(15, titleLabel, amountField, patientIdField, dateField, dueDateField, statusLayout,
				buttonLayout);
		fieldsLayout.setPadding(new Insets(20));
		fieldsLayout.setAlignment(Pos.CENTER);
		fieldsLayout.setStyle("-fx-background-color: #15919B;");

		Scene addInvoiceScene = new Scene(fieldsLayout, 400, 310);
		addInvoiceScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

		addInvoiceStage.setScene(addInvoiceScene);
		addInvoiceStage.show();
	}

	private void deleteInvoice(int invoiceId) {
		try {
			Connection con = db.getConnection().connectDB();
			String sql = "DELETE FROM invoice WHERE invoice_id = ?";
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, invoiceId);
			int rowsAffected = pstmt.executeUpdate();
			if (rowsAffected > 0) {
				showAlert(Alert.AlertType.INFORMATION, "Success", "Invoice deleted successfully.");
			} else {
				showAlert(Alert.AlertType.WARNING, "Not Found", "Invoice ID not found.");
			}
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete invoice.");
		}
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