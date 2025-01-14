package com.example.phase4_1220813_122856_1210475;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;

public class DoctorManagement {
    static dataBaseConnection db = new dataBaseConnection();
    static ObservableList<Doctors> doctorList = FXCollections.observableArrayList();
    static TableView<Doctors> doctorsTableView = new TableView<>();

    public static void readDoctors() {
        doctorList.clear();
        try {
            Connection con = db.getConnection().connectDB();
            String sql = "SELECT * FROM doctors"; // Ensure this query is correct for your table
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                // Ensure the doctor_id is being fetched properly as an integer
                int doctor_id = rs.getInt("doctor_id");
                String name = rs.getString("name");
                String speciality = rs.getString("speciality");
                long phonenumber = rs.getLong("phonenumber");
                String email = rs.getString("email");
                String date = rs.getString("date");

                // Add to the doctor list
                doctorList.add(new Doctors(doctor_id, name, speciality, phonenumber, email, date));
            }

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        doctorsTableView.setItems(doctorList);
    }

    public static BorderPane createDoctorsScreenLayout(Stage primaryStage, Scene homeScene) {
        BorderPane doctorsRoot = new BorderPane();
        doctorsRoot.setPrefSize(830, 600);
        doctorsRoot.setStyle("-fx-background-color: #15919B;");

        Label titleLabel = new Label("Doctor Management");
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: white;");
        VBox titleBox = new VBox(titleLabel);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(10));

        TableColumn<Doctors, Integer> idColumn = new TableColumn<>("ID");
        TableColumn<Doctors, String> nameColumn = new TableColumn<>("Name");
        TableColumn<Doctors, String> specialityColumn = new TableColumn<>("Speciality");
        TableColumn<Doctors, Integer> phoneColumn = new TableColumn<>("Phone Number");
        TableColumn<Doctors, String> emailColumn = new TableColumn<>("Email");
        TableColumn<Doctors, String> dateColumn = new TableColumn<>("Date");

        double tableWidth = 800;
        double columnWidth = tableWidth / 6;

        idColumn.setPrefWidth(columnWidth);
        nameColumn.setPrefWidth(columnWidth);
        specialityColumn.setPrefWidth(columnWidth);
        phoneColumn.setPrefWidth(columnWidth);
        emailColumn.setPrefWidth(columnWidth);
        dateColumn.setPrefWidth(columnWidth);

        idColumn.setCellValueFactory(new PropertyValueFactory<>("doctor_id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        specialityColumn.setCellValueFactory(new PropertyValueFactory<>("speciality"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phonenumber"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        doctorsTableView.getColumns().addAll(idColumn, nameColumn, specialityColumn, phoneColumn, emailColumn, dateColumn);

        Button homeButton = new Button("Home");
        homeButton.setOnAction(e -> primaryStage.setScene(homeScene));

        TextField searchDoctor=new TextField();
        searchDoctor.setPromptText("Search by id or name");
        Button addButton = new Button("Add");
        Button deleteButton = new Button("Delete");
        Button updateButton = new Button("Update");
        Button refreshButton = new Button("Refresh");

        HBox buttonLayout = new HBox(10, homeButton, searchDoctor,addButton, deleteButton, updateButton, refreshButton);
        buttonLayout.setAlignment(Pos.CENTER);
        buttonLayout.setPadding(new Insets(10));

        VBox centerLayout = new VBox(10, doctorsTableView, buttonLayout);
        centerLayout.setAlignment(Pos.CENTER);
        centerLayout.setPadding(new Insets(10));

        doctorsRoot.setTop(titleBox);
        doctorsRoot.setCenter(centerLayout);

        searchDoctor.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String query = searchDoctor.getText().trim();
                if (query.isEmpty()) {
                    readDoctors();
                } else {
                    DoctorManagement.searchDoctor(query);
                }
            }
        });

        deleteButton.setOnAction(e -> {
            Doctors selectedDoctor = doctorsTableView.getSelectionModel().getSelectedItem();
            if (selectedDoctor != null) {
                deleteDoctor(selectedDoctor);
                readDoctors();
            } else {
                showAlert(Alert.AlertType.WARNING, "Input Error", "No doctor selected to delete.");
            }
        });

        refreshButton.setOnAction(e -> readDoctors());

        addButton.setOnAction(e -> addDoctor());

        updateButton.setOnAction(e -> {
            Doctors selectedDoctor = doctorsTableView.getSelectionModel().getSelectedItem();
            if (selectedDoctor != null) {
                updateDoctorDetails(selectedDoctor);
            } else {
                showAlert(Alert.AlertType.WARNING, "Input Error", "No doctor selected to update.");
            }
        });

        return doctorsRoot;
    }
    public static void searchDoctor(String query) {
        ObservableList<Doctors> searchResults = FXCollections.observableArrayList();

        try (Connection con = db.getConnection().connectDB()) {
            String sql = "SELECT doctor_id, name, speciality, phonenumber, email, date " +
                    "FROM doctors WHERE doctor_id LIKE ? OR name LIKE ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, "%" + query + "%");
            pstmt.setString(2, "%" + query + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                searchResults.add(new Doctors(
                        rs.getInt("doctor_id"),
                        rs.getString("name"),
                        rs.getString("speciality"),
                        rs.getLong("phonenumber"),
                        rs.getString("email"),
                        rs.getString("date")
                ));
            }

            doctorsTableView.setItems(searchResults);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Search Error", "Failed to execute search query.");
        }
    }

    private static void deleteDoctor(Doctors doctor) {
        try {
            Connection con = db.getConnection().connectDB();
            String sql = "DELETE FROM doctors WHERE doctor_id = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, doctor.getDoctor_id()); // Ensure it's being set as an int
            pstmt.executeUpdate();
            pstmt.close();
            con.close();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Doctor deleted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete doctor.");
        }
    }

    private static void addDoctor() {
        Stage addDoctorStage = new Stage();
        addDoctorStage.setTitle("Add New Doctor");

        Label titleLabel = new Label("Add New Doctor");
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: white;");

        TextField nameField = new TextField();
        nameField.setPromptText("Enter Name");

        TextField specialityField = new TextField();
        specialityField.setPromptText("Enter Speciality");

        TextField phoneField = new TextField();
        phoneField.setPromptText("Enter Phone Number");

        TextField emailField = new TextField();
        emailField.setPromptText("Enter Email");

        TextField dateField = new TextField();
        dateField.setPromptText("Enter Date of Joining (YYYY-MM-DD)");

        Button saveButton = new Button("Save");
        Button cancelButton = new Button("Cancel");

        saveButton.setOnAction(e -> {
            try {
                String name = nameField.getText();
                String speciality = specialityField.getText();
                String phone = phoneField.getText();
                String email = emailField.getText();
                String date = dateField.getText();

                Connection con = db.getConnection().connectDB();
                String sql = "INSERT INTO doctors (name, speciality, phonenumber, email, date) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pstmt = con.prepareStatement(sql);
                pstmt.setString(1, name);
                pstmt.setString(2, speciality);
                pstmt.setString(3, phone);
                pstmt.setString(4, email);
                pstmt.setString(5, date);

                pstmt.executeUpdate();
                pstmt.close();
                con.close();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Doctor added successfully.");
                readDoctors();
                addDoctorStage.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add doctor.");
            }
        });

        cancelButton.setOnAction(e -> addDoctorStage.close());

        VBox fieldsLayout = new VBox(10, nameField, specialityField, phoneField, emailField, dateField, saveButton, cancelButton);
        fieldsLayout.setPadding(new Insets(20));
        fieldsLayout.setAlignment(Pos.CENTER);
        fieldsLayout.setStyle("-fx-background-color: #15919B;");

        Scene scene = new Scene(fieldsLayout, 400, 300);
        addDoctorStage.setScene(scene);
        addDoctorStage.show();
    }

    private static void updateDoctorDetails(Doctors doctor) {
        Stage updateDoctorStage = new Stage();
        updateDoctorStage.setTitle("Update Doctor Details");

        Label titleLabel = new Label("Update Doctor Details");
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: white;");

        TextField nameField = new TextField(doctor.getName());
        nameField.setPromptText("Enter Name");
        TextField specialityField = new TextField(doctor.getSpeciality());
        specialityField.setPromptText("Enter Speciality");
        TextField phoneField = new TextField(String.valueOf(doctor.getPhonenumber()));
        phoneField.setPromptText("Enter Phone Number");
        TextField emailField = new TextField(doctor.getEmail());
        emailField.setPromptText("Enter Email");
        TextField dateField = new TextField(doctor.getDate());
        dateField.setPromptText("Enter Date");

        Button saveButton = new Button("Save");
        Button cancelButton = new Button("Cancel");

        saveButton.setOnAction(e -> {
            try {
                String name = nameField.getText();
                String speciality = specialityField.getText();
                String phone = phoneField.getText();
                String email = emailField.getText();
                String date = dateField.getText();

                Connection con = db.getConnection().connectDB();
                String sql = "UPDATE doctors SET name = ?, speciality = ?, phonenumber = ?, email = ?, date = ? WHERE doctor_id = ?";
                PreparedStatement pstmt = con.prepareStatement(sql);
                pstmt.setString(1, name);
                pstmt.setString(2, speciality);
                pstmt.setString(3, phone);
                pstmt.setString(4, email);
                pstmt.setString(5, date);
                pstmt.setInt(6, doctor.getDoctor_id());

                pstmt.executeUpdate();
                pstmt.close();
                con.close();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Doctor updated successfully.");
                readDoctors();
                updateDoctorStage.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update doctor.");
            }
        });

        cancelButton.setOnAction(e -> updateDoctorStage.close());

        VBox fieldsLayout = new VBox(10, nameField, specialityField, phoneField, emailField, dateField, saveButton, cancelButton);
        fieldsLayout.setPadding(new Insets(20));
        fieldsLayout.setAlignment(Pos.CENTER);
        fieldsLayout.setStyle("-fx-background-color: #15919B;");

        Scene scene = new Scene(fieldsLayout, 400, 300);
        updateDoctorStage.setScene(scene);
        updateDoctorStage.show();
    }

    private static void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
