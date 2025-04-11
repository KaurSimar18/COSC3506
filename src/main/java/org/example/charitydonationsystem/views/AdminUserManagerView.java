package org.example.charitydonationsystem.views;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.charitydonationsystem.components.Sidebar;
import org.example.charitydonationsystem.dao.UserDAO;
import org.example.charitydonationsystem.models.User;

import java.util.List;

/**
 * Admin view to manage all users (edit, delete).
 */
public class AdminUserManagerView {

    public void show(Stage stage, int userId) {
        Label heading = new Label("Manage Users");
        heading.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        VBox userListBox = new VBox(10);
        userListBox.setPadding(new Insets(10));

        List<User> users = UserDAO.getAllUsers();

        if (users.isEmpty()) {
            userListBox.getChildren().add(new Label("No users found."));
        } else {
            for (User u : users) {
                TextField nameField = new TextField(u.getFullName());
                TextField emailField = new TextField(u.getEmail());

                ComboBox<String> roleBox = new ComboBox<>();
                roleBox.getItems().addAll("admin", "donor", "fundraiser");
                roleBox.setValue(u.getRole());

                Button updateBtn = new Button("Update");
                Button deleteBtn = new Button("Delete");

                Label result = new Label();

                updateBtn.setOnAction(e -> {
                    u.setFullName(nameField.getText().trim());
                    u.setEmail(emailField.getText().trim());
                    u.setRole(roleBox.getValue());
                    UserDAO.updateUser(u);
                    result.setText("Updated!");
                });

                deleteBtn.setOnAction(e -> {
                    if (u.getUserId() == userId) {
                        result.setText("You can't delete yourself.");
                        return;
                    }
                    UserDAO.deleteUser(u.getUserId());
                    show(stage, userId); // refresh
                });

                HBox box = new HBox(10,
                        new Label("Username: " + u.getUsername()),
                        nameField,
                        emailField,
                        roleBox,
                        updateBtn,
                        deleteBtn,
                        result
                );
                box.setPadding(new Insets(5));
                userListBox.getChildren().add(box);
            }
        }

        ScrollPane scrollPane = new ScrollPane(userListBox);
        scrollPane.setFitToWidth(true);

        VBox center = new VBox(20, heading, scrollPane);
        center.setPadding(new Insets(30));

        BorderPane root = new BorderPane();
        root.setLeft(Sidebar.createSidebar(stage, "admin", userId));
        root.setCenter(center);

        stage.setScene(new Scene(root, 1000, 600));
        stage.setTitle("User Manager");
    }
}

