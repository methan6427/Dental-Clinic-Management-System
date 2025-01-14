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

public class AppointmentsManagement {
    static dataBaseConnection db = new dataBaseConnection();
    static ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();
    static TableView<Appointment> appointmentsTableView = new TableView<>();

    public static void readAppointments() {
        appointmentList.clear();
        try (Connection con = db.getConnection().connectDB()) {
            String sql = "SELECT * FROM appointments";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                appointmentList.add(new Appointment(
                        rs.getInt("appointment_id"),
                        rs.getString("date"),
                        rs.getString("time"),
                        rs.getString("purpose"),
                        rs.getInt("cid"),
                        rs.getInt("doctor_id")
                ));
            }

            appointmentsTableView.setItems(appointmentList);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load appointments data.");
        }
    }

    public static BorderPane createAppointmentsScreenLayout(Stage primaryStage, Scene homeScene) {
        BorderPane appointmentsRoot = new BorderPane();
        appointmentsRoot.setPrefSize(830, 600);
        appointmentsRoot.setStyle("-fx-background-color: #15919B;");

        Label titleLabel = new Label("Appointments Management");
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: white;");
        VBox titleBox = new VBox(titleLabel);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(10));

        TableColumn<Appointment, Integer> idColumn = new TableColumn<>("ID");
        TableColumn<Appointment, String> dateColumn = new TableColumn<>("Date");
        TableColumn<Appointment, String> timeColumn = new TableColumn<>("Time");
        TableColumn<Appointment, String> purposeColumn = new TableColumn<>("Purpose");
        TableColumn<Appointment, Integer> cidColumn = new TableColumn<>("Patient ID");
        TableColumn<Appointment, Integer> doctorIdColumn = new TableColumn<>("Doctor ID");

        double tableWidth = 800;
        double columnWidth = tableWidth / 6;

        idColumn.setPrefWidth(columnWidth);
        dateColumn.setPrefWidth(columnWidth);
        timeColumn.setPrefWidth(columnWidth);
        purposeColumn.setPrefWidth(columnWidth);
        cidColumn.setPrefWidth(columnWidth);
        doctorIdColumn.setPrefWidth(columnWidth);

        idColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        purposeColumn.setCellValueFactory(new PropertyValueFactory<>("purpose"));
        cidColumn.setCellValueFactory(new PropertyValueFactory<>("cid"));
        doctorIdColumn.setCellValueFactory(new PropertyValueFactory<>("doctorId"));

        appointmentsTableView.getColumns().addAll(idColumn, dateColumn, timeColumn, purposeColumn, cidColumn, doctorIdColumn);

        Button homeButton = new Button("Home");
        homeButton.setOnAction(e -> primaryStage.setScene(homeScene));

        TextField searchAppointment = new TextField();
        searchAppointment.setPromptText("Search by Appointment ID");
        Button addButton = new Button("Add");
        Button deleteButton = new Button("Delete");
        Button updateButton = new Button("Update");
        Button refreshButton = new Button("Refresh");

        HBox buttonLayout = new HBox(10, homeButton, searchAppointment, addButton, deleteButton,updateButton, refreshButton);
        buttonLayout.setAlignment(Pos.CENTER);
        buttonLayout.setPadding(new Insets(10));

        VBox centerLayout = new VBox(10, appointmentsTableView, buttonLayout);
        centerLayout.setAlignment(Pos.CENTER);
        centerLayout.setPadding(new Insets(10));

        appointmentsRoot.setTop(titleBox);
        appointmentsRoot.setCenter(centerLayout);

        deleteButton.setOnAction(e -> {
            Appointment selectedAppointment = appointmentsTableView.getSelectionModel().getSelectedItem();
            if (selectedAppointment != null) {
                deleteAppointment(selectedAppointment);
                readAppointments();
            } else {
                showAlert(Alert.AlertType.WARNING, "Input Error", "No appointment selected to delete.");
            }
        });
        searchAppointment.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String query = searchAppointment.getText().trim();
                if (query.isEmpty()) {
                    readAppointments();
                } else {
                    AppointmentsManagement.searchAppointment(query);
                }
            }
        });
        addButton.setOnAction(e -> addAppointment());
        updateButton.setOnAction(e -> {
            Appointment selectedAppointment = appointmentsTableView.getSelectionModel().getSelectedItem();
            if (selectedAppointment != null) {
                updateAppointmentDetails(selectedAppointment);
            } else {
                showAlert(Alert.AlertType.WARNING, "Input Error", "No appointment selected to update.");
            }
        });
        refreshButton.setOnAction(e -> readAppointments());

        return appointmentsRoot;
    }

    private static void addAppointment() {
        Stage addAppointmentStage = new Stage();
        addAppointmentStage.setTitle("Add New Appointment");

        VBox fieldsLayout = createAppointmentForm(null, addAppointmentStage, false);
        Scene scene = new Scene(fieldsLayout, 400, 300);
        addAppointmentStage.setScene(scene);
        addAppointmentStage.show();
    }
    private static void updateAppointmentDetails(Appointment appointment) {
        Stage updateAppointmentStage = new Stage();
        updateAppointmentStage.setTitle("Update Appointment Details");

        VBox fieldsLayout = createAppointmentForm(appointment, updateAppointmentStage, true);
        Scene scene = new Scene(fieldsLayout, 400, 300);
        updateAppointmentStage.setScene(scene);
        updateAppointmentStage.show();
    }
    private static VBox createAppointmentForm(Appointment appointment, Stage stage, boolean isUpdate) {
        TextField dateField = new TextField(isUpdate ? appointment.getDate() : "");
        dateField.setPromptText("Enter Date (YYYY-MM-DD)");
        TextField timeField = new TextField(isUpdate ? appointment.getTime() : "");
        timeField.setPromptText("Enter Time (HH:MM:SS)");
        TextField purposeField = new TextField(isUpdate ? appointment.getPurpose() : "");
        purposeField.setPromptText("Enter Purpose");
        TextField cidField = new TextField(isUpdate ? String.valueOf(appointment.getCid()) : "");
        cidField.setPromptText("Enter Patient ID (CID)");
        TextField doctorIdField = new TextField(isUpdate ? String.valueOf(appointment.getDoctorId()) : "");
        doctorIdField.setPromptText("Enter Doctor ID");

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            try (Connection con = db.getConnection().connectDB()) {
                String sql;
                if (isUpdate) {
                    sql = "UPDATE appointments SET date = ?, time = ?, purpose = ?, cid = ?, doctor_id = ? WHERE appointment_id = ?";
                } else {
                    sql = "INSERT INTO appointments (date, time, purpose, cid, doctor_id) VALUES (?, ?, ?, ?, ?)";
                }
                PreparedStatement pstmt = con.prepareStatement(sql);
                pstmt.setString(1, dateField.getText());
                pstmt.setString(2, timeField.getText());
                pstmt.setString(3, purposeField.getText());
                pstmt.setInt(4, Integer.parseInt(cidField.getText()));
                pstmt.setInt(5, Integer.parseInt(doctorIdField.getText()));
                if (isUpdate) {
                    pstmt.setInt(6, appointment.getAppointmentId());
                }
                pstmt.executeUpdate();
                showAlert(Alert.AlertType.INFORMATION, "Success", isUpdate ? "Appointment updated successfully." : "Appointment added successfully.");
                readAppointments();
                stage.close();
            } catch (SQLException | ClassNotFoundException ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to save appointment data.");
            }
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> stage.close());

        VBox fieldsLayout = new VBox(10, dateField, timeField, purposeField, cidField, doctorIdField, saveButton, cancelButton);
        fieldsLayout.setPadding(new Insets(20));
        fieldsLayout.setAlignment(Pos.CENTER);
        fieldsLayout.setStyle("-fx-background-color: #15919B;");
        return fieldsLayout;
    }
    private static void deleteAppointment(Appointment appointment) {
        try (Connection con = db.getConnection().connectDB()) {
            String sql = "DELETE FROM appointments WHERE appointment_id = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, appointment.getAppointmentId());
            pstmt.executeUpdate();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Appointment deleted successfully.");
            readAppointments();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "SQL error while deleting appointment.");
        }
    }
    public static void searchAppointment(String query)  {
        ObservableList<Appointment> searchResults = FXCollections.observableArrayList();

        try (Connection con = db.getConnection().connectDB()) {
            String sql = "SELECT * FROM appointments WHERE appointment_id LIKE ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, "%" + query + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                searchResults.add(new Appointment(
                        rs.getInt("appointment_id"),
                        rs.getString("date"),
                        rs.getString("time"),
                        rs.getString("purpose"),
                        rs.getInt("cid"),
                        rs.getInt("doctor_id")
                ));
            }

            appointmentsTableView.setItems(searchResults);
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
