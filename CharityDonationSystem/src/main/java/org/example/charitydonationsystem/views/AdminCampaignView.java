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

public class AdminCampaignView {

    public void show(Stage stage, int userId) {
        Label heading = new Label("All Campaigns");
        heading.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        VBox campaignBox = new VBox(10);
        campaignBox.setPadding(new Insets(10));

        List<String> campaigns = CampaignDAO.getAllCampaignSummaries();

        if (campaigns.isEmpty()) {
            campaignBox.getChildren().add(new Label("No campaigns yet."));
        } else {
            for (String summary : campaigns) {
                campaignBox.getChildren().add(new Label(summary));
            }
        }

        ScrollPane scrollPane = new ScrollPane(campaignBox);
        scrollPane.setFitToWidth(true);

        VBox content = new VBox(15, heading, scrollPane);
        content.setPadding(new Insets(30));

        BorderPane root = new BorderPane();
        root.setLeft(Sidebar.createSidebar(stage, "admin", userId));
        root.setCenter(content);

        stage.setScene(new Scene(root, 900, 600));
        stage.setTitle("Campaigns Overview");
    }
}

