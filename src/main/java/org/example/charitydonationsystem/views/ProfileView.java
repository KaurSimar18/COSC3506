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

/**
 * ProfileView allows any user (admin, donor, or fundraiser) to view and update their personal profile.
 * Fields include full name and email. Username is shown as read-only.
 */
public class ProfileView {

    /**
     * Displays the profile screen with user info, and allows updates.
     *
     * @param stage  the JavaFX stage
     * @param role   the user's role (admin/donor/fundraiser)
     * @param userId the ID of the logged-in user
     */
    public void show(Stage stage, String role, int userId) {
        // Header
        Label heading = new Label("User Profile");
        heading.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Read-only username field
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setDisable(true);

        // Editable fields
        TextField fullNameField = new TextField();
        fullNameField.setPromptText("Full Name");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: green;");

        // Save button
        Button saveBtn = new Button("Save Changes");
        saveBtn.setOnAction(e -> {
            try (Connection conn = DBUtil.getConnection()) {
                String updateSql = "UPDATE users SET full_name = ?, email = ? WHERE user_id = ?";
                PreparedStatement stmt = conn.prepareStatement(updateSql);
                stmt.setString(1, fullNameField.getText().trim());
                stmt.setString(2, emailField.getText().trim());
                stmt.setInt(3, userId);
                stmt.executeUpdate();

                statusLabel.setText("Profile updated successfully!");
                statusLabel.setStyle("-fx-text-fill: green;");
            } catch (Exception ex) {
                ex.printStackTrace();
                statusLabel.setText("Update failed.");
                statusLabel.setStyle("-fx-text-fill: red;");
            }
        });

        // Form layout
        VBox form = new VBox(12, heading, usernameField, fullNameField, emailField, saveBtn, statusLabel);
        form.setPadding(new Insets(30));
        form.setStyle("-fx-alignment: center-left;");

        // Load user data from DB on screen open
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT username, full_name, email FROM users WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                usernameField.setText(rs.getString("username"));
                fullNameField.setText(rs.getString("full_name"));
                emailField.setText(rs.getString("email"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Final layout with sidebar
        BorderPane root = new BorderPane();
        root.setLeft(Sidebar.createSidebar(stage, role, userId));
        root.setCenter(form);

        stage.setScene(new Scene(root, 800, 500));
        stage.setTitle("Profile");
    }
}
