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
import org.example.charitydonationsystem.dao.SubscriptionDAO;
import org.example.charitydonationsystem.models.Campaign;

import java.util.List;

public class DonorCampaignBrowser {

    public void show(Stage stage, int userId) {
        Label heading = new Label("Available Campaigns");
        heading.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

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
            Button subBtn = new Button(); // will be set dynamically
            Label msg = new Label();

            // Set subscribe/unsubscribe button
            boolean isSubbed = SubscriptionDAO.isSubscribed(userId, c.getId());
            if (isSubbed) {
                subBtn.setText("Unsubscribe");
                subBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
            } else {
                subBtn.setText("Subscribe");
                subBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
            }

            // Handle subscription toggle
            subBtn.setOnAction(e -> {
                if (SubscriptionDAO.isSubscribed(userId, c.getId())) {
                    SubscriptionDAO.unsubscribe(userId, c.getId());
                    msg.setText("Unsubscribed.");
                } else {
                    SubscriptionDAO.subscribe(userId, c.getId());
                    msg.setText("Subscribed!");
                }
                show(stage, userId); // refresh view
            });

            // Handle donation
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

            VBox box = new VBox(6,
                    title, desc,
                    new HBox(10, amountField, donateBtn),
                    subBtn,
                    msg
            );
            box.setPadding(new Insets(10));
            box.setStyle("-fx-border-color: #ccc; -fx-background-color: #fdfdfd; -fx-border-radius: 4; -fx-background-radius: 4;");

            campaignList.getChildren().add(box);
        }

        ScrollPane scrollPane = new ScrollPane(campaignList);
        scrollPane.setFitToWidth(true);

        VBox content = new VBox(15, heading, scrollPane);
        content.setPadding(new Insets(30));

        BorderPane root = new BorderPane();
        root.setLeft(Sidebar.createSidebar(stage, "donor", userId));
        root.setCenter(content);

        stage.setScene(new Scene(root, 900, 600));
        stage.setTitle("Browse Campaigns");
    }
}
