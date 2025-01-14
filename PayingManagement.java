package com.example.phase4_1220813_122856_1210475;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;

public class PayingManagement {
	dataBaseConnection db = new dataBaseConnection();
	ObservableList<Paying> payingList = FXCollections.observableArrayList();
	TableView<Paying> payingTableView = new TableView<>();

	public void readPaying() {
		payingList.clear();
		try {
			Connection con = db.getConnection().connectDB();
			String sql = "SELECT payment_id, type, amount_paid, currency, invoice_id, payment_date, employee_ssn FROM paying";
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				payingList.add(new Paying(rs.getInt("payment_id"), rs.getString("type"), rs.getDouble("amount_paid"),
						rs.getString("currency"), rs.getInt("invoice_id"), rs.getString("payment_date"),
						rs.getInt("employee_ssn")));
			}

			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		payingTableView.setItems(payingList);
	}

	public BorderPane createPayingManagementLayout(Stage primaryStage, Scene homeScene) {
		BorderPane payingRoot = new BorderPane();
		payingRoot.setPrefSize(800, 600);
		payingRoot.setStyle("-fx-background-color: #15919B;");

		Label titleLabel = new Label("Paying Management");
		titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: white;");
		StackPane titlePane = new StackPane(titleLabel);
		titlePane.setStyle("-fx-padding: 10;");

		payingTableView.setEditable(true);

		double tableWidth = 900;
		int columnCount = 7;
		double columnWidth = tableWidth / columnCount;

		TableColumn<Paying, Integer> paymentIdCol = new TableColumn<>("Payment ID");
		paymentIdCol.setPrefWidth(columnWidth);
		paymentIdCol.setResizable(false);
		paymentIdCol.setCellValueFactory(new PropertyValueFactory<>("paymentId"));

		TableColumn<Paying, String> typeCol = new TableColumn<>("Type");
		typeCol.setPrefWidth(columnWidth);
		typeCol.setResizable(false);
		typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
		typeCol.setCellFactory(TextFieldTableCell.forTableColumn());

		TableColumn<Paying, Double> amountPaidCol = new TableColumn<>("Amount Paid");
		amountPaidCol.setPrefWidth(columnWidth);
		amountPaidCol.setResizable(false);
		amountPaidCol.setCellValueFactory(new PropertyValueFactory<>("amountPaid"));
		amountPaidCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));

		TableColumn<Paying, String> currencyCol = new TableColumn<>("Currency");
		currencyCol.setPrefWidth(columnWidth);
		currencyCol.setResizable(false);
		currencyCol.setCellValueFactory(new PropertyValueFactory<>("currency"));
		currencyCol.setCellFactory(TextFieldTableCell.forTableColumn());

		TableColumn<Paying, Integer> invoiceIdCol = new TableColumn<>("Invoice ID");
		invoiceIdCol.setPrefWidth(columnWidth);
		invoiceIdCol.setResizable(false);
		invoiceIdCol.setCellValueFactory(new PropertyValueFactory<>("invoiceId"));

		TableColumn<Paying, String> paymentDateCol = new TableColumn<>("Payment Date");
		paymentDateCol.setPrefWidth(columnWidth);
		paymentDateCol.setResizable(false);
		paymentDateCol.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));

		TableColumn<Paying, Integer> employeeSsnCol = new TableColumn<>("Employee SSN");
		employeeSsnCol.setPrefWidth(columnWidth);
		employeeSsnCol.setResizable(false);
		employeeSsnCol.setCellValueFactory(new PropertyValueFactory<>("employeeSsn"));

		payingTableView.getColumns().addAll(paymentIdCol, typeCol, amountPaidCol, currencyCol, paymentDateCol,
				invoiceIdCol, employeeSsnCol);

		HBox payingBottomLayout = new HBox(10);
		payingBottomLayout.setStyle("-fx-padding: 10; -fx-alignment: center;");

		Button addPayingButton = new Button("Add Payment");
		Button deletePayingButton = new Button("Delete Payment");
		Button refreshPayingButton = new Button("Refresh");
		refreshPayingButton.setOnAction(e -> showEditPayingDialog());
		Button statsButton = new Button("Show Statistics");
		statsButton.setOnAction(e -> showStatisticsWindow());

		TextField searchPayingField = new TextField();
		searchPayingField.setPromptText("Enter Payment ID");
		Button searchPayingButton = new Button("Search");
		Button homePayingButton = new Button("Home");
		homePayingButton.setOnAction(e -> primaryStage.setScene(homeScene));

		payingBottomLayout.getChildren().addAll(homePayingButton, searchPayingField, addPayingButton,
				deletePayingButton, searchPayingButton, refreshPayingButton, statsButton);

		payingRoot.setTop(titlePane);
		payingRoot.setCenter(payingTableView);
		payingRoot.setBottom(payingBottomLayout);

		addPayingButton.setOnAction(e -> showAddPayingDialog());
		deletePayingButton.setOnAction(e -> {
			String idText = searchPayingField.getText();
			if (!idText.isEmpty()) {
				int id = Integer.parseInt(idText);
				deletePaying(id);
				readPaying();
			}
		});
		searchPayingButton.setOnAction(e -> {
			String idText = searchPayingField.getText();
			if (!idText.isEmpty()) {
				int id = Integer.parseInt(idText);
				searchPaying(id);
			}
		});

		return payingRoot;
	}

	private void showStatisticsWindow() {
		Stage statsStage = new Stage();
		statsStage.getIcons().add(new Image(getClass().getResourceAsStream("/statistic.png")));

		statsStage.setTitle("Payment Statistics");

		VBox statsLayout = new VBox(10);
		statsLayout.setPadding(new Insets(20));
		statsLayout.setStyle("-fx-background-color: #15919B; -fx-alignment: top-left;");

		Label statsTitleLabel = new Label("Statistics:");
		statsTitleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white; -fx-padding: 10;");

		Label statsLabel = new Label();
		statsLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white; -fx-padding: 10; -fx-wrap-text: true;");

		statsLabel.setText(getStatistics());

		statsLayout.getChildren().addAll(statsTitleLabel, statsLabel);

		Scene statsScene = new Scene(statsLayout, 400, 240);
		statsScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm()); // ربط الـ CSS بالمشهد
		statsStage.setScene(statsScene);
		statsStage.show();
	}

	private String getStatistics() {
		StringBuilder stats = new StringBuilder();
		try {
			Connection con = db.getConnection().connectDB();

			String totalPaymentsSql = "SELECT SUM(amount_paid) AS total FROM paying";
			PreparedStatement pstmtTotal = con.prepareStatement(totalPaymentsSql);
			ResultSet rsTotal = pstmtTotal.executeQuery();
			if (rsTotal.next()) {
				double totalPayments = rsTotal.getDouble("total");
				stats.append("Total Payments: ").append(totalPayments).append("\n");
			}

			String paymentsByTypeSql = "SELECT type, COUNT(*) AS count FROM paying GROUP BY type";
			PreparedStatement pstmtType = con.prepareStatement(paymentsByTypeSql);
			ResultSet rsType = pstmtType.executeQuery();
			while (rsType.next()) {
				String type = rsType.getString("type");
				int count = rsType.getInt("count");
				stats.append(type).append(": ").append(count).append(" payments\n");
			}

			String paymentsByEmployeeSql = "SELECT employee_ssn, SUM(amount_paid) AS total FROM paying GROUP BY employee_ssn";
			PreparedStatement pstmtEmployee = con.prepareStatement(paymentsByEmployeeSql);
			ResultSet rsEmployee = pstmtEmployee.executeQuery();
			while (rsEmployee.next()) {
				int employeeSsn = rsEmployee.getInt("employee_ssn");
				double total = rsEmployee.getDouble("total");
				stats.append("Employee SSN ").append(employeeSsn).append(": ").append(total)
						.append(" total payments\n");
			}

			rsTotal.close();
			rsType.close();
			rsEmployee.close();
			pstmtTotal.close();
			pstmtType.close();
			pstmtEmployee.close();
			con.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return stats.toString();
	}

	private void showEditPayingDialog() {
		Stage editPayingStage = new Stage();
		VBox editPayingLayout = new VBox(10);
		editPayingLayout.setPadding(new Insets(20));
		editPayingLayout.setStyle("-fx-background-color: #15919B; -fx-alignment: center;");

		editPayingStage.setTitle("Edit Payment");
		editPayingStage.getIcons().add(new Image(getClass().getResourceAsStream("/88.png")));

		Label idLabel = new Label("Enter Payment ID:");
		idLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");

		TextField paymentIdField = new TextField();
		paymentIdField.setPromptText("Enter Payment ID to Search");
		paymentIdField.getStyleClass().add("input-field");

		Button searchButton = new Button("Search");
		searchButton.setStyle(
				"-fx-background-color: linear-gradient(#C3C7F4, #15919B); -fx-text-fill: white; -fx-font-weight: bold;");

		ToggleGroup typeToggleGroup = new ToggleGroup();
		RadioButton cashButton = new RadioButton("Cash");
		RadioButton cardButton = new RadioButton("Card");
		RadioButton insuranceButton = new RadioButton("Insurance");

		cashButton.setToggleGroup(typeToggleGroup);
		cardButton.setToggleGroup(typeToggleGroup);
		insuranceButton.setToggleGroup(typeToggleGroup);
		cashButton.setStyle("-fx-text-fill: white;");
		cardButton.setStyle("-fx-text-fill: white;");
		insuranceButton.setStyle("-fx-text-fill: white;");

		HBox typeBox = new HBox(10, cashButton, cardButton, insuranceButton);
		typeBox.setAlignment(Pos.CENTER);

		TextField amountPaidField = new TextField();
		amountPaidField.setPromptText("Amount Paid");
		amountPaidField.getStyleClass().add("input-field");

		TextField currencyField = new TextField();
		currencyField.setPromptText("Currency");
		currencyField.getStyleClass().add("input-field");

		TextField invoiceIdField = new TextField();
		invoiceIdField.setPromptText("Invoice ID");
		invoiceIdField.getStyleClass().add("input-field");

		TextField employeeSsnField = new TextField();
		employeeSsnField.setPromptText("Employee SSN");
		employeeSsnField.getStyleClass().add("input-field");

		DatePicker paymentDateField = new DatePicker();
		paymentDateField.setPromptText("Payment Date");
		paymentDateField.getStyleClass().add("date-picker");

		Button saveButton = new Button("Save");
		saveButton.setStyle(
				"-fx-background-color: linear-gradient(#C3C7F4, #15919B); -fx-text-fill: white; -fx-font-weight: bold;");

		searchButton.setOnAction(e -> {
			if (paymentIdField.getText().isEmpty()) {
				showAlert(Alert.AlertType.WARNING, "Input Error", "Please enter a Payment ID.");
				return;
			}
			try {
				int paymentId = Integer.parseInt(paymentIdField.getText());

				Connection con = db.getConnection().connectDB();
				String sql = "SELECT * FROM paying WHERE payment_id = ?";
				PreparedStatement pstmt = con.prepareStatement(sql);
				pstmt.setInt(1, paymentId);
				ResultSet rs = pstmt.executeQuery();

				if (rs.next()) {
					String type = rs.getString("type");
					if (type.equalsIgnoreCase("Cash"))
						cashButton.setSelected(true);
					else if (type.equalsIgnoreCase("Card"))
						cardButton.setSelected(true);
					else if (type.equalsIgnoreCase("Insurance"))
						insuranceButton.setSelected(true);

					amountPaidField.setText(String.valueOf(rs.getDouble("amount_paid")));
					currencyField.setText(rs.getString("currency"));
					invoiceIdField.setText(String.valueOf(rs.getInt("invoice_id")));
					paymentDateField.setValue(LocalDate.parse(rs.getString("payment_date")));
					employeeSsnField.setText(String.valueOf(rs.getInt("employee_ssn")));
				} else {
					showAlert(Alert.AlertType.WARNING, "Not Found", "Payment with ID " + paymentId + " not found.");
				}

				rs.close();
				pstmt.close();
				con.close();
			} catch (NumberFormatException ex) {
				showAlert(Alert.AlertType.WARNING, "Input Error", "Please enter a valid numeric Payment ID.");
			} catch (Exception ex) {
				ex.printStackTrace();
				showAlert(Alert.AlertType.ERROR, "Error", "Failed to retrieve payment data.");
			}
		});

		saveButton.setOnAction(e -> {
			if (paymentIdField.getText().isEmpty() || typeToggleGroup.getSelectedToggle() == null
					|| amountPaidField.getText().isEmpty() || currencyField.getText().isEmpty()
					|| invoiceIdField.getText().isEmpty() || paymentDateField.getValue() == null
					|| employeeSsnField.getText().isEmpty()) {
				showAlert(Alert.AlertType.WARNING, "Input Error", "Please fill in all required fields.");
				return;
			}
			try {
				int paymentId = Integer.parseInt(paymentIdField.getText());
				String type = ((RadioButton) typeToggleGroup.getSelectedToggle()).getText();
				double amountPaid = Double.parseDouble(amountPaidField.getText());
				String currency = currencyField.getText();
				int invoiceId = Integer.parseInt(invoiceIdField.getText());
				int employeeSsn = Integer.parseInt(employeeSsnField.getText());
				String paymentDate = paymentDateField.getValue().toString();

				if (!currency.matches("[a-zA-Z]+")) {
					showAlert(Alert.AlertType.WARNING, "Input Error", "Currency must only contain letters.");
					return;
				}

				if (!isInvoiceExist(invoiceId)) {
					showAlert(Alert.AlertType.WARNING, "Invoice Not Found",
							"Invoice with ID " + invoiceId + " does not exist.");
					return;
				}

				try (Connection con = db.getConnection().connectDB()) {
					String sql = "SELECT type FROM employee WHERE snn = ?";
					PreparedStatement pstmt = con.prepareStatement(sql);
					pstmt.setInt(1, employeeSsn);
					ResultSet rs = pstmt.executeQuery();

					if (rs.next()) {
						String employeeType = rs.getString("type");
						if (!"Accountant".equalsIgnoreCase(employeeType)) {
							showAlert(Alert.AlertType.ERROR, "Invalid Employee", "The Employee is not an Accountant.");
							rs.close();
							pstmt.close();
							return;
						}
					} else {
						showAlert(Alert.AlertType.ERROR, "Not Found",
								"Employee with SSN " + employeeSsn + " not found.");
						rs.close();
						pstmt.close();
						return;
					}
					rs.close();
					pstmt.close();
				}

				Connection con = db.getConnection().connectDB();
				String sql = "UPDATE paying SET type = ?, amount_paid = ?, currency = ?, invoice_id = ?, payment_date = ?, employee_ssn = ? WHERE payment_id = ?";
				PreparedStatement pstmt = con.prepareStatement(sql);
				pstmt.setString(1, type);
				pstmt.setDouble(2, amountPaid);
				pstmt.setString(3, currency);
				pstmt.setInt(4, invoiceId);
				pstmt.setString(5, paymentDate);
				pstmt.setInt(6, employeeSsn);
				pstmt.setInt(7, paymentId);

				pstmt.executeUpdate();
				con.close();
				showAlert(Alert.AlertType.INFORMATION, "Success", "Payment details updated successfully.");
				readPaying();
				editPayingStage.close();
			} catch (NumberFormatException ex) {
				showAlert(Alert.AlertType.WARNING, "Input Error", "Please enter valid numeric values.");
			} catch (Exception ex) {
				ex.printStackTrace();
				showAlert(Alert.AlertType.ERROR, "Error", "Failed to update payment details.");
			}
		});

		editPayingLayout.getChildren().addAll(idLabel, paymentIdField, searchButton, typeBox, amountPaidField,
				currencyField, invoiceIdField, employeeSsnField, paymentDateField, saveButton);

		Scene editPayingScene = new Scene(editPayingLayout, 400, 425);
		editPayingScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
		editPayingStage.setScene(editPayingScene);
		editPayingStage.show();
	}

	private void deletePaying(int id) {
		try {
			Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
			confirmAlert.setTitle("Delete Confirmation");
			confirmAlert.setHeaderText("Are you sure you want to delete this payment?");
			confirmAlert.setContentText("Payment ID: " + id);

			if (confirmAlert.showAndWait().get() == ButtonType.OK) {
				Connection con = db.getConnection().connectDB();
				String sql = "DELETE FROM paying WHERE payment_id=?";
				PreparedStatement pstmt = con.prepareStatement(sql);
				pstmt.setInt(1, id);
				int rowsAffected = pstmt.executeUpdate();
				con.close();

				if (rowsAffected > 0) {
					showAlert(Alert.AlertType.INFORMATION, "Success", "Payment deleted successfully!");
				} else {
					showAlert(Alert.AlertType.ERROR, "Error", "No payment found with ID: " + id);
				}
			} else {
				showAlert(Alert.AlertType.INFORMATION, "Cancelled", "Deletion cancelled.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while deleting the payment.");
		}
	}

	private void searchPaying(int id) {
		TableView<Paying> searchResultTable = new TableView<>();
		ObservableList<Paying> searchResultList = FXCollections.observableArrayList();

		int columnCount = 7;

		TableColumn<Paying, Integer> paymentIdCol = new TableColumn<>("Payment ID");
		TableColumn<Paying, String> typeCol = new TableColumn<>("Type");
		TableColumn<Paying, Double> amountPaidCol = new TableColumn<>("Amount Paid");
		TableColumn<Paying, String> currencyCol = new TableColumn<>("Currency");
		TableColumn<Paying, Integer> invoiceIdCol = new TableColumn<>("Invoice ID");
		TableColumn<Paying, String> paymentDateCol = new TableColumn<>("Payment Date");
		TableColumn<Paying, Integer> employeeSsnCol = new TableColumn<>("Employee SSN");

		paymentIdCol.setCellValueFactory(new PropertyValueFactory<>("paymentId"));
		typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
		amountPaidCol.setCellValueFactory(new PropertyValueFactory<>("amountPaid"));
		currencyCol.setCellValueFactory(new PropertyValueFactory<>("currency"));
		invoiceIdCol.setCellValueFactory(new PropertyValueFactory<>("invoiceId"));
		paymentDateCol.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
		employeeSsnCol.setCellValueFactory(new PropertyValueFactory<>("employeeSsn"));

		searchResultTable.getColumns().addAll(paymentIdCol, typeCol, amountPaidCol, currencyCol, invoiceIdCol,
				paymentDateCol, employeeSsnCol);

		searchResultTable.widthProperty().addListener((obs, oldWidth, newWidth) -> {
			double columnWidth = (newWidth.doubleValue() - 2) / columnCount;
			paymentIdCol.setPrefWidth(columnWidth);
			typeCol.setPrefWidth(columnWidth);
			amountPaidCol.setPrefWidth(columnWidth);
			currencyCol.setPrefWidth(columnWidth);
			invoiceIdCol.setPrefWidth(columnWidth);
			paymentDateCol.setPrefWidth(columnWidth);
			employeeSsnCol.setPrefWidth(columnWidth);
		});

		try {
			Connection con = db.getConnection().connectDB();
			String sql = "SELECT payment_id, type, amount_paid, currency, invoice_id, payment_date, employee_ssn FROM paying WHERE payment_id=?";
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, id);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				searchResultList.add(new Paying(rs.getInt("payment_id"), rs.getString("type"),
						rs.getDouble("amount_paid"), rs.getString("currency"), rs.getInt("invoice_id"),
						rs.getString("payment_date"), rs.getInt("employee_ssn")));
			} else {
				showAlert(Alert.AlertType.WARNING, "Not Found", "No Payment found with ID: " + id);
				rs.close();
				pstmt.close();
				con.close();
				return;
			}

			searchResultTable.setItems(searchResultList);
			rs.close();
			pstmt.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Stage resultStage = new Stage();
		resultStage.getIcons().add(new Image(getClass().getResourceAsStream("/99.png")));

		resultStage.setTitle("Search Results");
		BorderPane resultLayout = new BorderPane();
		resultLayout.setCenter(searchResultTable);

		Scene resultScene = new Scene(resultLayout, 830, 400);

		resultScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

		resultStage.setScene(resultScene);
		resultStage.show();
	}

	private void showAddPayingDialog() {
		Stage addPayingStage = new Stage();
		addPayingStage.setTitle("Add New Payment");
		addPayingStage.getIcons().add(new Image(getClass().getResourceAsStream("/888.png")));

		VBox layout = new VBox(10);
		layout.getStyleClass().add("root");
		layout.setStyle("-fx-padding: 20; -fx-alignment: center; -fx-spacing: 10;");

		Label typeLabel = new Label("Payment Type:");
		typeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");

		ToggleGroup paymentTypeGroup = new ToggleGroup();
		RadioButton cashRadioButton = new RadioButton("Cash");
		cashRadioButton.setToggleGroup(paymentTypeGroup);
		cashRadioButton.setSelected(true);
		cashRadioButton.setStyle("-fx-text-fill: white;");

		RadioButton cardRadioButton = new RadioButton("Card");
		cardRadioButton.setToggleGroup(paymentTypeGroup);
		cardRadioButton.setStyle("-fx-text-fill: white;");

		RadioButton insuranceRadioButton = new RadioButton("Insurance");
		insuranceRadioButton.setToggleGroup(paymentTypeGroup);
		insuranceRadioButton.setStyle("-fx-text-fill: white;");

		HBox paymentTypeLayout = new HBox(10, cashRadioButton, cardRadioButton, insuranceRadioButton);
		paymentTypeLayout.setStyle("-fx-alignment: center;");

		TextField amountPaidField = new TextField();
		amountPaidField.setPromptText("Amount Paid");
		amountPaidField.getStyleClass().add("text-field");

		TextField currencyField = new TextField();
		currencyField.setPromptText("Currency");
		currencyField.getStyleClass().add("text-field");

		TextField invoiceIdField = new TextField();
		invoiceIdField.setPromptText("Invoice ID");
		invoiceIdField.getStyleClass().add("text-field");

		DatePicker paymentDateField = new DatePicker();
		paymentDateField.setPromptText("Payment Date");
		paymentDateField.getStyleClass().add("date-picker");

		TextField employeeSSNField = new TextField();
		employeeSSNField.setPromptText("Employee SSN");
		employeeSSNField.getStyleClass().add("text-field");

		Button addButton = new Button("Add");
		addButton.setStyle("-fx-background-color: linear-gradient(#C3C7F4, #15919B); " + "-fx-text-fill: white; "
				+ "-fx-font-weight: bold;");

		addButton.setOnAction(e -> {
			try {
				String paymentType = ((RadioButton) paymentTypeGroup.getSelectedToggle()).getText();
				double amountPaid = Double.parseDouble(amountPaidField.getText());
				String currency = currencyField.getText();
				int invoiceId = Integer.parseInt(invoiceIdField.getText());
				int employeeSSN = Integer.parseInt(employeeSSNField.getText());

				if (paymentDateField.getValue() == null) {
					showAlert(Alert.AlertType.ERROR, "Input Error", "Please select a valid payment date.");
					return;
				}
				String paymentDate = paymentDateField.getValue().toString();

				if (currency.isEmpty() || !currency.matches("[a-zA-Z]+")) {
					showAlert(Alert.AlertType.ERROR, "Input Error", "Currency must contain only letters.");
					return;
				}

				Connection con = db.getConnection().connectDB();
				String checkEmployeeSQL = "SELECT type FROM employee WHERE snn = ?";
				PreparedStatement checkEmployeeStmt = con.prepareStatement(checkEmployeeSQL);
				checkEmployeeStmt.setInt(1, employeeSSN);
				ResultSet rs = checkEmployeeStmt.executeQuery();

				if (!rs.next()) {
					showAlert(Alert.AlertType.ERROR, "Invalid Employee",
							"Employee with SSN " + employeeSSN + " does not exist.");
					rs.close();
					checkEmployeeStmt.close();
					con.close();
					return;
				}

				String employeeType = rs.getString("type");
				if (!"Accountant".equalsIgnoreCase(employeeType)) {
					showAlert(Alert.AlertType.ERROR, "Invalid Employee", "The Employee is not an Accountant.");
					rs.close();
					checkEmployeeStmt.close();
					con.close();
					return;
				}

				rs.close();
				checkEmployeeStmt.close();

				if (!isInvoiceExist(invoiceId)) {
					showAlert(Alert.AlertType.ERROR, "Invoice Not Found",
							"The invoice with ID " + invoiceId + " does not exist.");
					return;
				}

				String sql = "INSERT INTO paying (type, amount_paid, currency, invoice_id, payment_date, employee_ssn) VALUES (?, ?, ?, ?, ?, ?)";
				PreparedStatement pstmt = con.prepareStatement(sql);
				pstmt.setString(1, paymentType);
				pstmt.setDouble(2, amountPaid);
				pstmt.setString(3, currency);
				pstmt.setInt(4, invoiceId);
				pstmt.setString(5, paymentDate);
				pstmt.setInt(6, employeeSSN);
				pstmt.executeUpdate();
				con.close();

				readPaying();
				showAlert(Alert.AlertType.INFORMATION, "Success", "Payment added successfully!");
				addPayingStage.close();
			} catch (NumberFormatException ex) {
				showAlert(Alert.AlertType.ERROR, "Input Error",
						"Amount Paid, Invoice ID, and Employee SSN must be numeric.");
			} catch (Exception ex) {
				ex.printStackTrace();
				showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while adding the payment.");
			}
		});

		Button cancelButton = new Button("Cancel");
		cancelButton.setStyle("-fx-background-color: linear-gradient(#C3C7F4, #15919B); " + "-fx-text-fill: white; "
				+ "-fx-font-weight: bold;");
		cancelButton.setOnAction(e -> addPayingStage.close());

		HBox buttonLayout = new HBox(10, addButton, cancelButton);
		buttonLayout.setStyle("-fx-alignment: center;");

		layout.getChildren().addAll(typeLabel, paymentTypeLayout, amountPaidField, currencyField, invoiceIdField,
				employeeSSNField, paymentDateField, buttonLayout);

		Scene scene = new Scene(layout, 400, 350);
		scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
		addPayingStage.setScene(scene);
		addPayingStage.show();
	}

	private boolean isInvoiceExist(int invoiceId) {
		try {
			Connection con = db.getConnection().connectDB();
			String sql = "SELECT 1 FROM invoice WHERE invoice_id = ?";
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, invoiceId);
			ResultSet rs = pstmt.executeQuery();
			boolean exists = rs.next();
			rs.close();
			pstmt.close();
			con.close();
			return exists;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
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