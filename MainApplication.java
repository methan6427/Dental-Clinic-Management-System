package com.example.phase4_1220813_122856_1210475;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class MainApplication extends Application {
    EmployeeManagement employeeManagement = new EmployeeManagement();
    InvoiceManagement invoiceManagement = new InvoiceManagement();
    SupplierManagement supplierManagement = new SupplierManagement();
    StockManagement stockManagement = new StockManagement();
    PayingManagement payingManagement = new PayingManagement();
    ProductManagement productManagement = new ProductManagement();
    PatientsManagement patientsManagement = new PatientsManagement();
    DoctorManagement doctorManagement = new DoctorManagement();
    AppointmentsManagement appointmentsManagement = new AppointmentsManagement();
    Scene homeScene, employeeScene, supplierScene, stockScene, payingScene, productScene, invoiceScene, patientScene, doctorScene, appointmentsScene;
    String userType;

    @Override
    public void start(Stage primaryStage) {
        showLoginScreen(primaryStage);
    }

    private void showLoginScreen(Stage primaryStage) {
        LoginUI loginUI = new LoginUI();
        loginUI.setOnLoginSuccess(() -> {
            userType = loginUI.getUserType();
            showMainUI(primaryStage);
        });
        Scene loginScene = loginUI.createLoginScene(primaryStage);
        primaryStage.setScene(loginScene);
        primaryStage.setTitle("Login");
        primaryStage.show();
    }

    private void showMainUI(Stage primaryStage) {
        employeeManagement.readEmployee();
        invoiceManagement.readInvoices();
        supplierManagement.readSublayer();
        stockManagement.readStock();
        payingManagement.readPaying();
        productManagement.readProduct();
        PatientsManagement.readPatients();
        DoctorManagement.readDoctors();
        AppointmentsManagement.readAppointments();

        primaryStage.setTitle("Management System");

        ListView<String> menu = new ListView<>();
        menu.getItems().addAll("Appointments","Patients","Employees","Doctors","Supplier", "Stock", "Paying", "Invoice", "Product", "Log Out");
        menu.setStyle("-fx-font-size: 16px; -fx-padding: 10;");

        BorderPane root = new BorderPane();
        root.setLeft(menu);

        ImageView backgroundImage = new ImageView(new Image(getClass().getResourceAsStream("/55.png")));
        backgroundImage.setFitWidth(300);
        backgroundImage.setFitHeight(300);
        backgroundImage.setPreserveRatio(true);

        Label textLabel1 = new Label("Bright Smile Dental");
        textLabel1.setStyle(
                "-fx-font-size: 41px; -fx-text-fill: #ffffff; -fx-font-family: 'Georgia'; -fx-font-weight: bold;");

        Label textLabel2 = new Label("clinic");
        textLabel2.setStyle(
                "-fx-font-size: 50px; -fx-text-fill: #ffffff; -fx-font-family: 'Georgia'; -fx-font-style: italic; -fx-font-weight: bold;");

        Pane fixedPane = new Pane();
        fixedPane.getChildren().addAll(backgroundImage, textLabel1, textLabel2);

        backgroundImage.setLayoutX(395);
        backgroundImage.setLayoutY(204);
        textLabel1.setLayoutX(50);
        textLabel1.setLayoutY(50);
        textLabel2.setLayoutX(70);
        textLabel2.setLayoutY(100);

        root.setCenter(fixedPane);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #15919B, #C3C7F4);");

        homeScene = new Scene(root, 900, 500);
        homeScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        primaryStage.setScene(homeScene);
        primaryStage.setResizable(false);
        primaryStage.show();

        employeeScene = new Scene(
                new BorderPane(employeeManagement.createEmployeeManagementLayout(primaryStage, homeScene)), 1200, 600);

        supplierScene = new Scene(
                new BorderPane(supplierManagement.createSupplierManagementLayout(primaryStage, homeScene)), 1200, 600);
        stockScene = new Scene(new BorderPane(stockManagement.createStockManagementLayoutWithTabPane(primaryStage, homeScene)),
                850, 600);
        payingScene = new Scene(new BorderPane(payingManagement.createPayingManagementLayout(primaryStage, homeScene)),
                940, 600);
        productScene = new Scene(
                new BorderPane(productManagement.createProductManagementLayout(primaryStage, homeScene)), 1200, 600);
        invoiceScene = new Scene(
                new BorderPane(invoiceManagement.createInvoiceManagementLayout(primaryStage, homeScene)), 850, 600);
        /***/
        patientScene = new Scene(new BorderPane(PatientsManagement.createPatientsScreenLayout(primaryStage, homeScene)), 1200, 600);
        doctorScene=new Scene(new BorderPane(doctorManagement.createDoctorsScreenLayout(primaryStage, homeScene)), 1200, 600);
        appointmentsScene=new Scene(new BorderPane(AppointmentsManagement.createAppointmentsScreenLayout(primaryStage, homeScene)), 1200, 600);

        applyBackgroundStyles();

        menu.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            handleMenuSelection(primaryStage, newValue);
        });

        if ("Manager".equals(userType)) {
            menu.getItems().add("Add User");
        }
    }

    private void handleMenuSelection(Stage primaryStage, String newValue) {
        switch (newValue) {
            case "Appointments":
                primaryStage.setScene(appointmentsScene);
                break;
            case "Patients":
                primaryStage.setScene(patientScene);
                break;
            case "Employees":
                primaryStage.setScene(employeeScene);
                break;
            case "Doctors":
                primaryStage.setScene(doctorScene);
                break;
            case "Supplier":
                primaryStage.setScene(supplierScene);
                break;
            case "Stock":
                primaryStage.setScene(stockScene);
                break;
            case "Paying":
                primaryStage.setScene(payingScene);
                break;
            case "Invoice":
                primaryStage.setScene(invoiceScene);
                break;
            case "Product":
                primaryStage.setScene(productScene);
                break;
            case "Log Out":
                logOut(primaryStage);
                break;
            case "Add User":
                showAddUserDialog();
                break;
            default:
                System.out.println("Unknown menu selection: " + newValue);
        }
    }

    private void applyBackgroundStyles() {
        String backgroundStyle = "-fx-background-color: linear-gradient(to bottom, #15919B, #C3C7F4);";
        String stylesheet = getClass().getResource("/styles.css").toExternalForm();

        if (patientScene != null) {
            patientScene.getRoot().setStyle(backgroundStyle);
            patientScene.getStylesheets().add(stylesheet);
        }
        if (employeeScene != null) {
            employeeScene.getRoot().setStyle(backgroundStyle);
            employeeScene.getStylesheets().add(stylesheet);
        }

        if (supplierScene != null) {
            supplierScene.getRoot().setStyle(backgroundStyle);
            supplierScene.getStylesheets().add(stylesheet);
        }

        if (stockScene != null) {
            stockScene.getRoot().setStyle(backgroundStyle);
            stockScene.getStylesheets().add(stylesheet);
        }

        if (payingScene != null) {
            payingScene.getRoot().setStyle(backgroundStyle);
            payingScene.getStylesheets().add(stylesheet);
        }

        if (productScene != null) {
            productScene.getRoot().setStyle(backgroundStyle);
            productScene.getStylesheets().add(stylesheet);
        }

        if (invoiceScene != null) {
            invoiceScene.getRoot().setStyle(backgroundStyle);
            invoiceScene.getStylesheets().add(stylesheet);
        }
        if (appointmentsScene != null) {
            appointmentsScene.getRoot().setStyle(backgroundStyle);
            appointmentsScene.getStylesheets().add(stylesheet);
        }
        if (doctorScene != null) {
            doctorScene.getRoot().setStyle(backgroundStyle);
            doctorScene.getStylesheets().add(stylesheet);
        }
    }

    private void logOut(Stage primaryStage) {
        userType = null;
        showLoginScreen(primaryStage);
    }

    private void showAddUserDialog() {
        Stage addUserStage = new Stage();
        addUserStage.setTitle("Add New User");
        addUserStage.getIcons().add(new Image(getClass().getResourceAsStream("/people.png")));

        VBox addUserLayout = new VBox(10);
        addUserLayout.setPadding(new Insets(10, 10, 10, 10));
        addUserLayout.setStyle("-fx-background-color: #15919B;");

        Label usernameLabel = new Label("Username:");
        usernameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        Label passwordLabel = new Label("Password:");
        passwordLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Label employeeIdLabel = new Label("Employee ID:");
        employeeIdLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        TextField employeeIdField = new TextField();
        employeeIdField.setPromptText("Employee ID");

        Button addUserBtn = new Button("Add User");
        addUserBtn.setStyle("-fx-background-color: linear-gradient(#C3C7F4, #15919B); " + "-fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-font-size: 14px;");

        addUserBtn.setOnAction(e -> {
            try {
                String username = usernameField.getText();
                String password = passwordField.getText();
                int employeeId = Integer.parseInt(employeeIdField.getText());

                if (addUser(username, password, employeeId)) {
                    showAlert(Alert.AlertType.INFORMATION, "User Added", "New user added successfully!");
                    addUserStage.close();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to add new user.");
                }
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.WARNING, "Input Error", "Please enter a valid Employee ID.");
            }
        });

        addUserLayout.getChildren().addAll(usernameLabel, usernameField, passwordLabel, passwordField, employeeIdLabel,
                employeeIdField, addUserBtn);

        Scene addUserScene = new Scene(addUserLayout, 300, 300);
        addUserScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        addUserStage.setScene(addUserScene);
        addUserStage.show();
    }

    private boolean addUser(String username, String password, int employeeId) {
        boolean isSuccess = false;
        try {
            Connection con = new dataBaseConnection().getConnection().connectDB();
            String sql = "INSERT INTO user (username, password, employee_id) VALUES (?, ?, ?)";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setInt(3, employeeId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                isSuccess = true;
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
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

    public static void main(String[] args) {
        launch(args);
    }
}