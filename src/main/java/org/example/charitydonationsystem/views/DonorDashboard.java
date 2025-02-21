package org.example.charitydonationsystem.views;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.charitydonationsystem.components.Sidebar;

/**
 * DonorDashboard is the home screen for users with the donor role.
 * It provides a welcoming message and encourages donors to explore campaigns.
 */
public class DonorDashboard {

    /**
     * Displays the Donor Dashboard screen after login.
     *
     * @param stage  the current window
     * @param userId the ID of the logged-in donor
     */
    public void show(Stage stage, int userId) {
        // Main welcome text
        Label welcome = new Label("Welcome to the Donor Dashboard!");
        welcome.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        // Helper instructions or call to action
        Label info = new Label("Use the sidebar to browse campaigns, donate, or view your donation history.");
        info.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

        // Layout container for the center
        VBox content = new VBox(15, welcome, info);
        content.setStyle("-fx-padding: 40; -fx-alignment: center-left;");

        // Page layout with sidebar
        BorderPane root = new BorderPane();
        root.setLeft(Sidebar.createSidebar(stage, "donor", userId));
        root.setCenter(content);

        // Load the scene
        Scene scene = new Scene(root, 800, 500);
        stage.setScene(scene);
        stage.setTitle("Donor Dashboard");
    }
}
