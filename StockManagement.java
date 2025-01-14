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

public class StockManagement {
	dataBaseConnection db = new dataBaseConnection();
	ObservableList<Stock> StockList = FXCollections.observableArrayList();
	TableView<Stock> stockTableView = new TableView<>();

	public void readStock() {
		StockList.clear();
		try {
			Connection con = db.getConnection().connectDB();
			String sql = "SELECT * FROM stock";
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				StockList.add(new Stock(rs.getInt("stockId"), rs.getString("shelfLocation"),
						rs.getDate("lastUpdate").toLocalDate(), rs.getInt("employeeSsn")));
			}
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		stockTableView.setItems(StockList);
	}

	public BorderPane createStockManagementLayoutWithTabPane(Stage primaryStage, Scene homeScene) {
		BorderPane root = new BorderPane();
		root.setPrefSize(800, 600);
		root.setStyle("-fx-background-color: #15919B;");

		TabPane tabPane = new TabPane();

		Tab stockManagementTab = new Tab("Stock Management");
		stockManagementTab.setClosable(false);

		BorderPane stockRoot = new BorderPane();
		stockRoot.setStyle("-fx-background-color: #15919B;");
		stockTableView.setEditable(true);

		Label titleLabel = new Label("Stock Management");
		titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
		titleLabel.setAlignment(Pos.CENTER);

		VBox titleBox = new VBox(10, titleLabel, stockTableView);
		titleBox.setAlignment(Pos.CENTER);
		titleBox.setPadding(new Insets(10));

		TableColumn<Stock, Integer> stockIdCol = new TableColumn<>("ID");
		TableColumn<Stock, String> shelfLocationCol = new TableColumn<>("Shelf Location");
		TableColumn<Stock, LocalDate> lastUpdateCol = new TableColumn<>("Last Update");
		TableColumn<Stock, Integer> employeeSsnCol = new TableColumn<>("Employee SSN");

		stockTableView.widthProperty().addListener((obs, oldWidth, newWidth) -> {
			double columnWidth = (newWidth.doubleValue() - 4) / 4;
			stockIdCol.setPrefWidth(columnWidth);
			shelfLocationCol.setPrefWidth(columnWidth);
			lastUpdateCol.setPrefWidth(columnWidth);
			employeeSsnCol.setPrefWidth(columnWidth);
		});

		stockIdCol.setCellValueFactory(new PropertyValueFactory<>("stockId"));
		shelfLocationCol.setCellValueFactory(new PropertyValueFactory<>("shelfLocation"));
		lastUpdateCol.setCellValueFactory(new PropertyValueFactory<>("lastUpdate"));
		employeeSsnCol.setCellValueFactory(new PropertyValueFactory<>("employeeSsn"));

		stockTableView.getColumns().addAll(stockIdCol, shelfLocationCol, lastUpdateCol, employeeSsnCol);
		stockTableView.setItems(StockList);

		HBox buttonLayout = new HBox(10);
		buttonLayout.setStyle("-fx-padding: 10; -fx-alignment: center;");

		Button addStockButton = new Button("Add Stock");
		Button deleteStockButton = new Button("Delete Stock");
		Button searchStockButton = new Button("Search for Stock");
		Button refreshStockButton = new Button("Refresh");

		refreshStockButton.setOnAction(e -> showSearchAndEditDialog());

		TextField searchStockField = new TextField();
		searchStockField.setPromptText("Enter Stock ID");

		Button homeStockButton = new Button("Home");
		homeStockButton.setOnAction(e -> primaryStage.setScene(homeScene));

		addStockButton.setOnAction(e -> showAddStockDialog());
		deleteStockButton.setOnAction(e -> {
			String idText = searchStockField.getText();
			if (!idText.isEmpty()) {
				int id = Integer.parseInt(idText);
				deleteStock(id);
				readStock();
			}
		});

		searchStockButton.setOnAction(e -> {
			String idText = searchStockField.getText();
			if (!idText.isEmpty()) {
				int id = Integer.parseInt(idText);
				searchStock(id);
			}
		});

		buttonLayout.getChildren().addAll(homeStockButton, searchStockField, addStockButton, deleteStockButton,
				searchStockButton, refreshStockButton);

		stockRoot.setCenter(titleBox);
		stockRoot.setBottom(buttonLayout);
		stockManagementTab.setContent(stockRoot);

		Tab statisticsTab = new Tab("Statistics");
		statisticsTab.setClosable(false);

		VBox statisticsLayout = createStockStatisticsLayout();
		statisticsTab.setContent(statisticsLayout);

		tabPane.getTabs().addAll(stockManagementTab, statisticsTab);

		root.setCenter(tabPane);

		return root;
	}

	public VBox createStockStatisticsLayout() {
		VBox statisticsLayout = new VBox(10);
		
		statisticsLayout.setPadding(new Insets(10));
		statisticsLayout.setAlignment(Pos.CENTER_LEFT);
		statisticsLayout.setStyle("-fx-background-color: #15919B;");

		Label titleLabel = new Label("Stock Statistics:");
		titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

		Label totalShelvesLabel = new Label("Total Shelves: ");
		Label totalStockRecordsLabel = new Label("Total Stock Records: ");
		Label latestUpdateLabel = new Label("Latest Stock Update: ");
		Label oldestUpdateLabel = new Label("Oldest Stock Update: ");
		Label totalEmployeesLabel = new Label("Total Employees with Stock Records: ");
		Label mostUsedShelfLabel = new Label("Most Used Shelf Location: ");
		Label uniqueShelvesLabel = new Label("Unique Shelf Locations: ");

		Label[] labels = { totalShelvesLabel, totalStockRecordsLabel, latestUpdateLabel, oldestUpdateLabel,
				totalEmployeesLabel, mostUsedShelfLabel, uniqueShelvesLabel };

		for (Label label : labels) {
			label.setStyle("-fx-text-fill: white;");
		}

		Button calculateStatisticsButton = new Button("Show Statistics");
		calculateStatisticsButton.setStyle(
				"-fx-background-color: linear-gradient(#C3C7F4, #15919B); -fx-text-fill: white; -fx-font-weight: bold;");

		calculateStatisticsButton.setOnAction(e -> {
			try (Connection con = db.getConnection().connectDB()) {
				String sql = """
						SELECT
						    COUNT(DISTINCT shelfLocation) AS total_shelves,
						    COUNT(*) AS total_stock_records,
						    MAX(lastUpdate) AS latest_update,
						    MIN(lastUpdate) AS oldest_update,
						    COUNT(DISTINCT employeeSsn) AS total_employees,
						    (SELECT shelfLocation FROM stock GROUP BY shelfLocation ORDER BY COUNT(*) DESC LIMIT 1) AS most_used_shelf,
						    COUNT(DISTINCT shelfLocation) AS unique_shelves
						FROM stock;
						""";

				PreparedStatement pstmt = con.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery();

				if (rs.next()) {
					totalShelvesLabel.setText("Total Shelves: " + rs.getInt("total_shelves"));
					totalStockRecordsLabel.setText("Total Stock Records: " + rs.getInt("total_stock_records"));
					latestUpdateLabel.setText("Latest Stock Update: " + rs.getDate("latest_update").toString());
					oldestUpdateLabel.setText("Oldest Stock Update: " + rs.getDate("oldest_update").toString());
					totalEmployeesLabel.setText("Total Employees with Stock Records: " + rs.getInt("total_employees"));
					mostUsedShelfLabel.setText("Most Used Shelf Location: " + rs.getString("most_used_shelf"));
					uniqueShelvesLabel.setText("Unique Shelf Locations: " + rs.getInt("unique_shelves"));
				}

				rs.close();
				pstmt.close();
			} catch (Exception ex) {
				ex.printStackTrace();
				showAlert(Alert.AlertType.ERROR, "Error", "Failed to retrieve stock statistics.");
			}
		});

		statisticsLayout.getChildren().addAll(titleLabel, totalShelvesLabel, totalStockRecordsLabel, latestUpdateLabel,
				oldestUpdateLabel, totalEmployeesLabel, mostUsedShelfLabel, uniqueShelvesLabel,
				calculateStatisticsButton);

		return statisticsLayout;
	}

	private void showSearchAndEditDialog() {
		Stage searchEditStage = new Stage();
		searchEditStage.setTitle("Search and Edit Stock");
		searchEditStage.getIcons().add(new Image(getClass().getResourceAsStream("/88.png")));

		VBox searchEditLayout = new VBox(10);
		searchEditLayout.setStyle("-fx-padding: 20;");

		TextField idField = new TextField();
		idField.setPromptText("Enter Stock ID");
		idField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue.matches("\\d*")) {
				idField.setText(newValue.replaceAll("[^\\d]", ""));
			}
		});

		Button searchButton = new Button("Search");
		searchButton.getStyleClass().add("action-button");

		TextField shelfLocationField = new TextField();
		shelfLocationField.setPromptText("Shelf Location");
		shelfLocationField.setDisable(true);

		DatePicker lastUpdateField = new DatePicker();
		lastUpdateField.setPromptText("Last Update");
		lastUpdateField.setDisable(true);

		TextField employeeSsnField = new TextField();
		employeeSsnField.setPromptText("Employee SSN");
		employeeSsnField.setDisable(true);

		Button saveButton = new Button("Save");
		saveButton.getStyleClass().add("action-button");
		saveButton.setDisable(true);

		searchButton.setOnAction(e -> {
			try {
				int id = Integer.parseInt(idField.getText());
				Connection con = db.getConnection().connectDB();
				String sql = "SELECT * FROM stock WHERE stockId = ?";
				PreparedStatement pstmt = con.prepareStatement(sql);
				pstmt.setInt(1, id);
				ResultSet rs = pstmt.executeQuery();

				if (rs.next()) {
					shelfLocationField.setText(rs.getString("shelfLocation"));
					lastUpdateField.setValue(rs.getDate("lastUpdate").toLocalDate());
					employeeSsnField.setText(String.valueOf(rs.getInt("employeeSsn")));

					shelfLocationField.setDisable(false);
					lastUpdateField.setDisable(false);
					employeeSsnField.setDisable(false);
					saveButton.setDisable(false);
				} else {
					showAlert(Alert.AlertType.WARNING, "Not Found", "Stock with ID " + id + " was not found.");
					shelfLocationField.clear();
					lastUpdateField.setValue(null);
					employeeSsnField.clear();
					shelfLocationField.setDisable(true);
					lastUpdateField.setDisable(true);
					employeeSsnField.setDisable(true);
					saveButton.setDisable(true);
				}

				rs.close();
				pstmt.close();
				con.close();
			} catch (Exception ex) {
				ex.printStackTrace();
				showAlert(Alert.AlertType.ERROR, "Error", "Failed to retrieve stock data.");
			}
		});

		saveButton.setOnAction(e -> {
			try {
				if (idField.getText().isEmpty()) {
					showAlert(Alert.AlertType.ERROR, "Input Error", "Stock ID cannot be empty.");
					return;
				}

				if (shelfLocationField.getText().isEmpty()) {
					showAlert(Alert.AlertType.ERROR, "Input Error", "Shelf Location cannot be empty.");
					return;
				}

				if (lastUpdateField.getValue() == null) {
					showAlert(Alert.AlertType.ERROR, "Input Error", "Last Update date must be selected.");
					return;
				}

				if (employeeSsnField.getText().isEmpty() || !employeeSsnField.getText().matches("\\d+")) {
					showAlert(Alert.AlertType.ERROR, "Input Error", "Employee SSN must be a valid number.");
					return;
				}

				int employeeSsn = Integer.parseInt(employeeSsnField.getText());

				// التحقق من نوع الموظف
				Connection con = db.getConnection().connectDB();
				String checkSql = "SELECT type FROM employee WHERE snn = ?";
				PreparedStatement checkStmt = con.prepareStatement(checkSql);
				checkStmt.setInt(1, employeeSsn);
				ResultSet checkRs = checkStmt.executeQuery();

				if (checkRs.next()) {
					String employeeType = checkRs.getString("type");
					if (!"worker".equalsIgnoreCase(employeeType)) {
						showAlert(Alert.AlertType.ERROR, "Invalid Employee", "The employee must be of type 'Worker'.");
						checkRs.close();
						checkStmt.close();
						con.close();
						return;
					}
				} else {
					showAlert(Alert.AlertType.ERROR, "Employee Not Found",
							"No employee found with SSN " + employeeSsn + ".");
					checkRs.close();
					checkStmt.close();
					con.close();
					return;
				}
				checkRs.close();
				checkStmt.close();

				int id = Integer.parseInt(idField.getText());
				String shelfLocation = shelfLocationField.getText();
				LocalDate lastUpdate = lastUpdateField.getValue();

				String updateSql = "UPDATE stock SET shelfLocation = ?, lastUpdate = ?, employeeSsn = ? WHERE stockId = ?";
				PreparedStatement updateStmt = con.prepareStatement(updateSql);
				updateStmt.setString(1, shelfLocation);
				updateStmt.setDate(2, java.sql.Date.valueOf(lastUpdate));
				updateStmt.setInt(3, employeeSsn);
				updateStmt.setInt(4, id);

				int rowsAffected = updateStmt.executeUpdate();

				if (rowsAffected > 0) {
					readStock();
					stockTableView.refresh();
					showAlert(Alert.AlertType.INFORMATION, "Success", "Stock details updated successfully.");
					searchEditStage.close();
				} else {
					showAlert(Alert.AlertType.WARNING, "Update Failed", "No stock found with the specified ID.");
				}

				updateStmt.close();
				con.close();
			} catch (NumberFormatException ex) {
				showAlert(Alert.AlertType.ERROR, "Input Error", "Stock ID and Employee SSN must be valid numbers.");
			} catch (Exception ex) {
				ex.printStackTrace();
				showAlert(Alert.AlertType.ERROR, "Error", "Failed to update stock details.");
			}
		});

		searchEditLayout.getChildren().addAll(idField, searchButton, shelfLocationField, employeeSsnField,
				lastUpdateField, saveButton);

		Scene searchEditScene = new Scene(searchEditLayout, 400, 265);
		searchEditScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
		searchEditStage.setScene(searchEditScene);
		searchEditStage.show();
	}

	private void deleteStock(int id) {
		try {
			Connection con = db.getConnection().connectDB();
			String sql = "DELETE FROM stock WHERE stockId = ?";
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, id);

			int affectedRows = pstmt.executeUpdate();

			pstmt.close();
			con.close();

			if (affectedRows > 0) {
				showAlert(Alert.AlertType.INFORMATION, "Success", "Stock with ID " + id + " was successfully deleted.");
			} else {
				showAlert(Alert.AlertType.WARNING, "Not Found",
						"Stock with ID " + id + " was not found in the database.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Error",
					"An error occurred while trying to delete the stock. Please try again.");
		}
	}

	private void searchStock(int id) {
		try {
			Connection con = db.getConnection().connectDB();
			String sql = "SELECT * FROM stock WHERE stockId = ?";
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, id);
			ResultSet rs = pstmt.executeQuery();

			ObservableList<Stock> searchResultList = FXCollections.observableArrayList();

			if (rs.next()) {
				int stockId = rs.getInt("stockId");
				String shelfLocation = rs.getString("shelfLocation");
				LocalDate lastUpdate = rs.getDate("lastUpdate").toLocalDate();
				int employeeSsn = rs.getInt("employeeSsn"); // قراءة employeeSsn

				searchResultList.add(new Stock(stockId, shelfLocation, lastUpdate, employeeSsn));
			}

			rs.close();
			pstmt.close();
			con.close();

			if (searchResultList.isEmpty()) {
				showAlert(Alert.AlertType.WARNING, "Stock Not Found", "The stock with ID " + id + " was not found.");
				return;
			}

			showSearchResultInTableView(searchResultList);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showSearchResultInTableView(ObservableList<Stock> searchResultList) {
		Stage resultStage = new Stage();
		resultStage.getIcons().add(new Image(getClass().getResourceAsStream("/99.png")));

		resultStage.setTitle("Search Results");

		TableView<Stock> resultTableView = new TableView<>();
		resultTableView.setItems(searchResultList);

		TableColumn<Stock, Integer> stockIdCol = new TableColumn<>("ID");
		stockIdCol.setCellValueFactory(new PropertyValueFactory<>("stockId"));
		stockIdCol.setPrefWidth(100);

		TableColumn<Stock, String> shelfLocationCol = new TableColumn<>("Shelf Location");
		shelfLocationCol.setCellValueFactory(new PropertyValueFactory<>("shelfLocation"));
		shelfLocationCol.setPrefWidth(200);

		TableColumn<Stock, LocalDate> lastUpdateCol = new TableColumn<>("Last Update");
		lastUpdateCol.setCellValueFactory(new PropertyValueFactory<>("lastUpdate"));
		lastUpdateCol.setPrefWidth(150);

		TableColumn<Stock, Integer> employeeSsnCol = new TableColumn<>("Employee SSN");
		employeeSsnCol.setCellValueFactory(new PropertyValueFactory<>("employeeSsn")); // إعداد employeeSsn
		employeeSsnCol.setPrefWidth(100);

		resultTableView.getColumns().addAll(stockIdCol, shelfLocationCol, lastUpdateCol, employeeSsnCol);

		VBox resultLayout = new VBox(10, resultTableView);
		resultLayout.setStyle("-fx-padding: 10;");

		Scene resultScene = new Scene(resultLayout, 600, 300);

		resultScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

		resultStage.setScene(resultScene);
		resultStage.show();
	}

	private void showAddStockDialog() {
		Stage addStockStage = new Stage();
		addStockStage.getIcons().add(new Image(getClass().getResourceAsStream("/111.png")));

		VBox addStockLayout = new VBox(20);
		addStockLayout.setStyle("-fx-padding: 20;");

		TextField shelfLocationField = new TextField();
		shelfLocationField.setPromptText("Shelf Location");
		shelfLocationField.getStyleClass().add("input-field");

		DatePicker lastUpdateField = new DatePicker();
		lastUpdateField.setPromptText("Last Update");
		lastUpdateField.getStyleClass().add("date-picker");

		TextField employeeSsnField = new TextField();
		employeeSsnField.setPromptText("Employee SSN");
		employeeSsnField.getStyleClass().add("input-field");

		Button addBtn = new Button("Add");
		addBtn.getStyleClass().add("action-button");

		addBtn.setOnAction(e -> {
			try {
				if (shelfLocationField.getText().isEmpty() || lastUpdateField.getValue() == null
						|| employeeSsnField.getText().isEmpty()) {
					showAlert(Alert.AlertType.ERROR, "Input Error", "All fields must be filled.");
					return;
				}

				int employeeSsn = Integer.parseInt(employeeSsnField.getText());

				Connection con = db.getConnection().connectDB();
				String checkEmployeeSql = "SELECT type FROM employee WHERE snn = ?";
				PreparedStatement checkEmployeeStmt = con.prepareStatement(checkEmployeeSql);
				checkEmployeeStmt.setInt(1, employeeSsn);
				ResultSet rs = checkEmployeeStmt.executeQuery();

				if (rs.next()) {
					String employeeType = rs.getString("type");
					if (!"Worker".equalsIgnoreCase(employeeType)) {
						showAlert(Alert.AlertType.ERROR, "Invalid Employee", "The Employee is not of type 'Work'.");
						rs.close();
						checkEmployeeStmt.close();
						con.close();
						return;
					}
				} else {
					showAlert(Alert.AlertType.ERROR, "Invalid Employee", "Employee SSN not found.");
					rs.close();
					checkEmployeeStmt.close();
					con.close();
					return;
				}

				rs.close();
				checkEmployeeStmt.close();

				String sql = "INSERT INTO stock (shelfLocation, lastUpdate, employeeSsn) VALUES (?, ?, ?)";
				PreparedStatement pstmt = con.prepareStatement(sql);
				pstmt.setString(1, shelfLocationField.getText());
				pstmt.setDate(2, java.sql.Date.valueOf(lastUpdateField.getValue()));
				pstmt.setInt(3, employeeSsn);
				pstmt.executeUpdate();

				StockList.add(new Stock(shelfLocationField.getText(), lastUpdateField.getValue(), employeeSsn));
				con.close();
				readStock();
				addStockStage.close();
			} catch (NumberFormatException ex) {
				showAlert(Alert.AlertType.ERROR, "Input Error", "Employee SSN must be a numeric value.");
			} catch (Exception ex) {
				ex.printStackTrace();
				showAlert(Alert.AlertType.ERROR, "Error", "Failed to add the stock.");
			}
		});

		addStockLayout.getChildren().addAll(shelfLocationField, employeeSsnField, lastUpdateField, addBtn);

		Scene addStockScene = new Scene(addStockLayout, 400, 300);
		addStockScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

		addStockStage.setScene(addStockScene);
		addStockStage.setTitle("Add Stock");

		addStockStage.setWidth(300);
		addStockStage.setHeight(250);

		addStockStage.show();
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