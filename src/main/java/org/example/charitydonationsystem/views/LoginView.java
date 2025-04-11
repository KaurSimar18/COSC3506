package org.example.charitydonationsystem.views;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.charitydonationsystem.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * LoginView handles the login screen for the application.
 * It authenticates users from the database and routes them to the correct dashboard.
 */
public class LoginView {

    /**
     * Displays the login interface and authenticates predefined users.
     *
     * @param stage the primary stage (JavaFX window)
     */
    public void start(Stage stage) {
        // Title label
        Label title = new Label("Charity Donation Login");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Username input
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        // Password input
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        // Status message label (success or error)
        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: red;");

        // Login button
        Button loginBtn = new Button("Login");
        loginBtn.setStyle("-fx-padding: 5 15 5 15;");

        loginBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            try (Connection conn = DBUtil.getConnection()) {
                String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, username);
                stmt.setString(2, password);

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String role = rs.getString("role");
                    int userId = rs.getInt("user_id");

                    statusLabel.setText("Login successful as " + role);
                    statusLabel.setStyle("-fx-text-fill: green;");

                    // Route based on role
                    switch (role) {
                        case "admin" -> new AdminDashboard().show(stage, userId);
                        case "donor" -> new DonorDashboard().show(stage, userId);
                        case "fundraiser" -> new FundraiserDashboard().show(stage, userId);
                    }
                } else {
                    statusLabel.setText("Invalid credentials!");
                    statusLabel.setStyle("-fx-text-fill: red;");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                statusLabel.setText("Database error.");
                statusLabel.setStyle("-fx-text-fill: red;");
            }
        });

        // Vertical layout for login screen
        VBox root = new VBox(12, title, usernameField, passwordField, loginBtn, statusLabel);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-alignment: center;");

        stage.setScene(new Scene(root, 400, 300));
        stage.setTitle("Login");
        stage.show();
    }
}
