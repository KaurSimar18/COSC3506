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

public class LoginView {

    public void start(Stage stage) {
        Label title = new Label("Charity Donation Login");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Label statusLabel = new Label();

        Button loginBtn = new Button("Login");
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
                    switch (role) {
                        case "admin" -> new AdminDashboard().show(stage, userId);
                        case "donor" -> new DonorDashboard().show(stage, userId);
                        case "fundraiser" -> new FundraiserDashboard().show(stage, userId);
                    }
                } else {
                    statusLabel.setText("Invalid credentials!");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                statusLabel.setText("Database error.");
            }
        });

        VBox root = new VBox(10, title, usernameField, passwordField, loginBtn, statusLabel);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-alignment: center;");

        stage.setScene(new Scene(root, 400, 300));
        stage.setTitle("Login");
        stage.show();
    }
}

