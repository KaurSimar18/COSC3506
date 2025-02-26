package org.example.charitydonationsystem.views;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.charitydonationsystem.components.Sidebar;
import org.example.charitydonationsystem.dao.CampaignDAO;
import org.example.charitydonationsystem.dao.DonationDAO;
import org.example.charitydonationsystem.models.Campaign;

import java.util.List;

public class DonorCampaignBrowser {

    public void show(Stage stage, int userId) {
        Label heading = new Label("Available Campaigns");
        heading.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        VBox campaignList = new VBox(15);
        campaignList.setPadding(new Insets(10));

        List<Campaign> campaigns = CampaignDAO.getAllActiveCampaigns();

        for (Campaign c : campaigns) {
            Label title = new Label(c.getTitle() + " | Goal: $" + c.getGoalAmount() + " | Raised: $" + c.getAmountRaised());
            TextArea desc = new TextArea(c.getDescription());
            desc.setWrapText(true);
            desc.setEditable(false);
            desc.setMaxHeight(60);

            TextField amountField = new TextField();
            amountField.setPromptText("Enter amount");

            Button donateBtn = new Button("Donate");
            Label msg = new Label();

            donateBtn.setOnAction(e -> {
                try {
                    double amount = Double.parseDouble(amountField.getText().trim());
                    DonationDAO.makeDonation(userId, c.getId(), amount);
                    msg.setText("Donation successful!");
                    show(stage, userId); // refresh
                } catch (Exception ex) {
                    ex.printStackTrace();
                    msg.setText("Donation failed.");
                }
            });

            VBox box = new VBox(5, title, desc, new HBox(10, amountField, donateBtn), msg);
            box.setPadding(new Insets(10));
            box.setStyle("-fx-border-color: gray; -fx-background-color: #fafafa;");
            campaignList.getChildren().add(box);
        }

        ScrollPane scrollPane = new ScrollPane(campaignList);
        scrollPane.setFitToWidth(true);

        BorderPane root = new BorderPane();
        root.setLeft(Sidebar.createSidebar(stage, "donor", userId));
        root.setCenter(new VBox(10, heading, scrollPane));

        stage.setScene(new Scene(root, 900, 600));
        stage.setTitle("Browse Campaigns");
    }
}

