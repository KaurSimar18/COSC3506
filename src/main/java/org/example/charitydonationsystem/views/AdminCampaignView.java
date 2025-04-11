package org.example.charitydonationsystem.views;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.charitydonationsystem.components.Sidebar;
import org.example.charitydonationsystem.dao.CampaignDAO;

import java.util.List;

/**
 * This view is used by Admins to see a list of all campaigns in the system.
 * It shows campaign titles, goals, raised amounts, statuses, and fundraiser names.
 */
public class AdminCampaignView {

    /**
     * Shows the campaign overview screen for the Admin role.
     *
     * @param stage  the JavaFX stage
     * @param userId ID of the currently logged-in admin (not used directly here)
     */
    public void show(Stage stage, int userId) {
        // Page heading
        Label heading = new Label("All Campaigns");
        heading.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Container for listing campaigns
        VBox campaignBox = new VBox(10);
        campaignBox.setPadding(new Insets(10));

        // Get campaign summaries from DAO
        List<String> campaigns = CampaignDAO.getAllCampaignSummaries();

        if (campaigns.isEmpty()) {
            campaignBox.getChildren().add(new Label("No campaigns available."));
        } else {
            for (String summary : campaigns) {
                Label itemLabel = new Label(summary);
                itemLabel.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 10px; -fx-border-radius: 5px;");
                campaignBox.getChildren().add(itemLabel);
            }
        }

        // Wrap the list in a scroll pane
        ScrollPane scrollPane = new ScrollPane(campaignBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        VBox content = new VBox(20, heading, scrollPane);
        content.setPadding(new Insets(30));

        // Root layout with sidebar
        BorderPane root = new BorderPane();
        root.setLeft(Sidebar.createSidebar(stage, "admin", userId));
        root.setCenter(content);

        // Set scene and title
        stage.setScene(new Scene(root, 900, 600));
        stage.setTitle("Campaigns Overview");
    }
}
