package com.example.phase4_1220813_122856_1210475;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginUI {

	dataBaseConnection db = new dataBaseConnection();
	private Runnable onLoginSuccess;
	private String userType;

	public Scene createLoginScene(Stage primaryStage) {
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/22.png")));

		grid.setStyle("-fx-background-color: linear-gradient(to bottom, #15919B, #C3C7F4);");

		Label userName = new Label("Username or Employee ID:");
		userName.setStyle(
				"-fx-text-fill: white; -fx-font-weight: bold; -fx-font-style: italic; -fx-font-family: 'Serif'; -fx-font-size: 16px;");
		grid.add(userName, 0, 1);

		TextField userTextField = new TextField();
		grid.add(userTextField, 1, 1);

		Label pw = new Label("Password:");
		pw.setStyle(
				"-fx-text-fill: white; -fx-font-weight: bold; -fx-font-style: italic; -fx-font-family: 'Serif'; -fx-font-size: 16px;");

		grid.add(pw, 0, 2);

		PasswordField pwBox = new PasswordField();
		grid.add(pwBox, 1, 2);

		Button loginButton = new Button("Sign in");
		loginButton.setStyle(
				"-fx-background-color: linear-gradient(to right, #15919B, #C3C7F4); -fx-font-weight: bold; -fx-text-fill: white;");
		grid.add(loginButton, 1, 3);

		loginButton.setOnAction(e -> {
			String usernameOrId = userTextField.getText();
			String password = pwBox.getText();
			if (login(usernameOrId, password)) {
				showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome!");
				if (onLoginSuccess != null) {
					onLoginSuccess.run();
				}
			} else {
				showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");
			}
		});
		return new Scene(grid, 400, 200);
	}

	public void setOnLoginSuccess(Runnable onLoginSuccess) {
		this.onLoginSuccess = onLoginSuccess;
	}

	public String getUserType() {
		return userType;
	}

	private boolean login(String usernameOrId, String password) {
		boolean isValid = false;
		try {
			Connection con = db.getConnection().connectDB();
			String sql;
			PreparedStatement pstmt;

			if (isNumeric(usernameOrId)) {
				sql = "SELECT e.type FROM user u JOIN employee e ON u.employee_id = e.snn WHERE u.employee_id = ? AND u.password = ?";
				pstmt = con.prepareStatement(sql);
				pstmt.setInt(1, Integer.parseInt(usernameOrId));
			} else {
				sql = "SELECT e.type FROM user u JOIN employee e ON u.employee_id = e.snn WHERE u.username = ? AND u.password = ?";
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, usernameOrId);
			}

			pstmt.setString(2, password);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				userType = rs.getString("type");
				isValid = true;
			}
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isValid;
	}

	private boolean isNumeric(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
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