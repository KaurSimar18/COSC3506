package org.example.charitydonationsystem.views;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.charitydonationsystem.components.Sidebar;
import org.example.charitydonationsystem.dao.DonationDAO;

import java.util.List;

public class DonorDonationHistory {

    public void show(Stage stage, int userId) {
        Label heading = new Label("Your Donation History");
        heading.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        double totalDonated = DonationDAO.getTotalDonatedByUser(userId);
        Label totalLabel = new Label("Total Donated: $" + totalDonated);
        totalLabel.setStyle("-fx-font-size: 14px; -fx-font-style: italic;");

        List<String> donationList = DonationDAO.getDonationsByUser(userId);
        VBox donationBox = new VBox(8);
        donationBox.setPadding(new Insets(10));

        if (donationList.isEmpty()) {
            donationBox.getChildren().add(new Label("No donations made yet."));
        } else {
            for (String donation : donationList) {
                donationBox.getChildren().add(new Label(donation));
            }
        }

        ScrollPane scrollPane = new ScrollPane(donationBox);
        scrollPane.setFitToWidth(true);

        VBox center = new VBox(15, heading, totalLabel, scrollPane);
        center.setPadding(new Insets(30));

        BorderPane root = new BorderPane();
        root.setLeft(Sidebar.createSidebar(stage, "donor", userId));
        root.setCenter(center);

        stage.setScene(new Scene(root, 800, 600));
        stage.setTitle("Donation History");
    }
}

