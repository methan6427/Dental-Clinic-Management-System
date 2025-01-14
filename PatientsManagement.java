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

public class PatientsManagement {
    static dataBaseConnection db = new dataBaseConnection();
    static ObservableList<Patient> patientList = FXCollections.observableArrayList();
    static TableView<Patient> patientsTableView = new TableView<>();

    public static void readPatients() {
        patientList.clear();
        try (Connection con = db.getConnection().connectDB()) {
            String sql = "SELECT cid, full_name, gender, email, phone_number, address, date_of_birth FROM patients";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                patientList.add(new Patient(
                        rs.getInt("cid"),
                        rs.getString("full_name"),
                        rs.getString("gender"),
                        rs.getString("email"),
                        rs.getString("phone_number"),
                        rs.getString("address"),
                        rs.getString("date_of_birth")
                ));
            }

            patientsTableView.setItems(patientList);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load patients data.");
        }
    }

    public static BorderPane createPatientsScreenLayout(Stage primaryStage, Scene homeScene) {
        BorderPane patientsRoot = new BorderPane();
        patientsRoot.setPrefSize(830, 600);
        patientsRoot.setStyle("-fx-background-color: #15919B;");

        Label titleLabel = new Label("Patient Management");
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: white;");
        VBox titleBox = new VBox(titleLabel);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(10));

        TableColumn<Patient, Integer> idColumn = new TableColumn<>("ID");
        TableColumn<Patient, String> nameColumn = new TableColumn<>("Name");
        TableColumn<Patient, String> genderColumn = new TableColumn<>("Gender");
        TableColumn<Patient, String> emailColumn = new TableColumn<>("Email");
        TableColumn<Patient, String> phoneColumn = new TableColumn<>("Phone Number");
        TableColumn<Patient, String> addressColumn = new TableColumn<>("Address");
        TableColumn<Patient, String> dobColumn = new TableColumn<>("Date of Birth");

        double tableWidth = 800;
        double columnWidth = tableWidth / 7;

        idColumn.setPrefWidth(columnWidth);
        nameColumn.setPrefWidth(columnWidth);
        genderColumn.setPrefWidth(columnWidth);
        dobColumn.setPrefWidth(columnWidth);
        phoneColumn.setPrefWidth(columnWidth);
        emailColumn.setPrefWidth(columnWidth);
        addressColumn.setPrefWidth(columnWidth);

        idColumn.setCellValueFactory(new PropertyValueFactory<>("cid"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        dobColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));

        patientsTableView.getColumns().addAll(idColumn, nameColumn, genderColumn, emailColumn, phoneColumn, addressColumn, dobColumn);

        Button homeButton = new Button("Home");
        homeButton.setOnAction(e -> primaryStage.setScene(homeScene));

        TextField searchPatient= new TextField();
        searchPatient.setPromptText("Search by cid or name");
        Button addButton = new Button("Add");
        Button deleteButton = new Button("Delete");
        Button updateButton = new Button("Update");
        Button refreshButton = new Button("Refresh");

        HBox buttonLayout = new HBox(10, homeButton,searchPatient, addButton, deleteButton, updateButton, refreshButton);
        buttonLayout.setAlignment(Pos.CENTER);
        buttonLayout.setPadding(new Insets(10));

        VBox centerLayout = new VBox(10, patientsTableView, buttonLayout);
        centerLayout.setAlignment(Pos.CENTER);
        centerLayout.setPadding(new Insets(10));

        patientsRoot.setTop(titleBox);
        patientsRoot.setCenter(centerLayout);

        deleteButton.setOnAction(e -> {
            Patient selectedPatient = patientsTableView.getSelectionModel().getSelectedItem();
            if (selectedPatient != null) {
                deletePatient(selectedPatient);
                readPatients();
            } else {
                showAlert(Alert.AlertType.WARNING, "Input Error", "No patient selected to delete.");
            }
        });

        searchPatient.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String query = searchPatient.getText().trim();
                if (query.isEmpty()) {
                    readPatients();
                } else {
                    PatientsManagement.searchPatient(query);
                }
            }
        });

        refreshButton.setOnAction(e -> readPatients());

        addButton.setOnAction(e -> addPatient());

        updateButton.setOnAction(e -> {
            Patient selectedPatient = patientsTableView.getSelectionModel().getSelectedItem();
            if (selectedPatient != null) {
                updatePatientDetails(selectedPatient);
            } else {
                showAlert(Alert.AlertType.WARNING, "Input Error", "No patient selected to update.");
            }
        });

        return patientsRoot;
    }

    private static void addPatient() {
        Stage addPatientStage = new Stage();
        addPatientStage.setTitle("Add New Patient");

        VBox fieldsLayout = createPatientForm(null, addPatientStage, false);
        Scene scene = new Scene(fieldsLayout, 400, 300);
        addPatientStage.setScene(scene);
        addPatientStage.show();
    }

    private static void updatePatientDetails(Patient patient) {
        Stage updatePatientStage = new Stage();
        updatePatientStage.setTitle("Update Patient Details");

        VBox fieldsLayout = createPatientForm(patient, updatePatientStage, true);
        Scene scene = new Scene(fieldsLayout, 400, 300);
        updatePatientStage.setScene(scene);
        updatePatientStage.show();
    }

    private static VBox createPatientForm(Patient patient, Stage stage, boolean isUpdate) {
        TextField nameField = new TextField(isUpdate ? patient.getFullName() : "");
        nameField.setPromptText("Enter Name");
        TextField genderField = new TextField(isUpdate ? patient.getGender() : "");
        genderField.setPromptText("Enter Gender");
        TextField dobField = new TextField(isUpdate ? patient.getDateOfBirth() : "");
        dobField.setPromptText("Enter DOB");
        TextField phoneField = new TextField(isUpdate ? patient.getPhoneNumber() : "");
        phoneField.setPromptText("Enter Phone Number");
        TextField emailField = new TextField(isUpdate ? patient.getEmail() : "");
        emailField.setPromptText("Enter Email");
        TextField addressField = new TextField(isUpdate ? patient.getAddress() : "");
        addressField.setPromptText("Enter Address");

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            try (Connection con = db.getConnection().connectDB()) {
                String sql;
                if (isUpdate) {
                    sql = "UPDATE patients SET full_name = ?, gender = ?, date_of_birth = ?, email = ?, phone_number = ?, address = ? WHERE cid = ?";
                } else {
                    sql = "INSERT INTO patients (full_name, gender, date_of_birth, email, phone_number, address) VALUES (?, ?, ?, ?, ?, ?)";
                }
                PreparedStatement pstmt = con.prepareStatement(sql);
                pstmt.setString(1, nameField.getText());
                pstmt.setString(2, genderField.getText());
                pstmt.setString(3, dobField.getText());
                pstmt.setString(4, emailField.getText());
                pstmt.setString(5, phoneField.getText());
                pstmt.setString(6, addressField.getText());
                if (isUpdate) {
                    pstmt.setInt(7, patient.getCid());
                }
                pstmt.executeUpdate();
                showAlert(Alert.AlertType.INFORMATION, "Success", isUpdate ? "Patient updated successfully." : "Patient added successfully.");
                readPatients();
                stage.close();
            } catch (SQLException | ClassNotFoundException ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to save patient data.");
            }
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> stage.close());

        VBox fieldsLayout = new VBox(10, nameField, genderField, dobField, phoneField, emailField, addressField, saveButton, cancelButton);
        fieldsLayout.setPadding(new Insets(20));
        fieldsLayout.setAlignment(Pos.CENTER);
        fieldsLayout.setStyle("-fx-background-color: #15919B;");
        return fieldsLayout;
    }

    private static void deletePatient(Patient patient) {
        try (Connection con = db.getConnection().connectDB()) {
            String sql = "DELETE FROM patients WHERE cid = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, patient.getCid());
            pstmt.executeUpdate();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Patient deleted successfully.");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "SQL error while deleting patient.");
        }
    }
    public static void searchPatient(String query) {
        ObservableList<Patient> searchResults = FXCollections.observableArrayList();

        try (Connection con = db.getConnection().connectDB()) {
            String sql = "SELECT cid, full_name, gender, email, phone_number, address, date_of_birth " +
                    "FROM patients WHERE cid LIKE ? OR full_name LIKE ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, "%" + query + "%");
            pstmt.setString(2, "%" + query + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                searchResults.add(new Patient(
                        rs.getInt("cid"),
                        rs.getString("full_name"),
                        rs.getString("gender"),
                        rs.getString("email"),
                        rs.getString("phone_number"),
                        rs.getString("address"),
                        rs.getString("date_of_birth")
                ));
            }

            patientsTableView.setItems(searchResults);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Search Error", "Failed to execute search query.");
        }
    }

    private static void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
