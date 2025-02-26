package org.example.charitydonationsystem.views;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.charitydonationsystem.DBUtil;
import org.example.charitydonationsystem.components.Sidebar;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ProfileView {

    public void show(Stage stage, String role, int userId) {
        Label heading = new Label("User Profile");
        heading.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setDisable(true);

        TextField fullNameField = new TextField();
        fullNameField.setPromptText("Full Name");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        TextField profilePicField = new TextField();
        profilePicField.setPromptText("Profile Picture Path");

        Label statusLabel = new Label();

        Button saveBtn = new Button("Save Changes");
        saveBtn.setOnAction(e -> {
            try (Connection conn = DBUtil.getConnection()) {
                String updateSql = "UPDATE users SET full_name = ?, email = ?, profile_picture = ? WHERE user_id = ?";
                PreparedStatement stmt = conn.prepareStatement(updateSql);
                stmt.setString(1, fullNameField.getText().trim());
                stmt.setString(2, emailField.getText().trim());
                stmt.setString(3, profilePicField.getText().trim());
                stmt.setInt(4, userId);
                stmt.executeUpdate();

                statusLabel.setText("Profile updated successfully!");
            } catch (Exception ex) {
                ex.printStackTrace();
                statusLabel.setText("Update failed.");
            }
        });

        VBox form = new VBox(10, heading, usernameField, fullNameField, emailField, profilePicField, saveBtn, statusLabel);
        form.setPadding(new Insets(30));
        form.setStyle("-fx-alignment: center-left;");

        // Load user data from DB
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT username, full_name, email, profile_picture FROM users WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                usernameField.setText(rs.getString("username"));
                fullNameField.setText(rs.getString("full_name"));
                emailField.setText(rs.getString("email"));
                profilePicField.setText(rs.getString("profile_picture"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        BorderPane root = new BorderPane();
        root.setLeft(Sidebar.createSidebar(stage, role, userId));
        root.setCenter(form);

        stage.setScene(new Scene(root, 800, 500));
        stage.setTitle("Profile");
    }
}
