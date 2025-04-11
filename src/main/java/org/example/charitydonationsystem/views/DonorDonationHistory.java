package org.example.charitydonationsystem.views;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.charitydonationsystem.components.Sidebar;
import org.example.charitydonationsystem.dao.DonationDAO;

import java.util.List;

/**
 * DonorDonationHistory shows the logged-in donor all their past donations.
 * It also displays the total amount they have donated and a visual chart.
 */
public class DonorDonationHistory {

    /**
     * Renders the donation history page for donors.
     *
     * @param stage  JavaFX stage
     * @param userId ID of the current donor
     */
    public void show(Stage stage, int userId) {
        // Header
        Label heading = new Label("Your Donation History");
        heading.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Total donated amount
        double totalDonated = DonationDAO.getTotalDonatedByUser(userId);
        Label totalLabel = new Label("Total Donated: $" + totalDonated);
        totalLabel.setStyle("-fx-font-size: 14px; -fx-font-style: italic; -fx-text-fill: #444;");

        // Bar chart: Donations by campaign
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Campaign");
        yAxis.setLabel("Amount Donated");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Your Donations by Campaign");

        XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();
        dataSeries.setName("Donations");

        List<String> donations = DonationDAO.getDonationsByUser(userId);
        for (String entry : donations) {
            try {
                // format: • Campaign Title — $50.0 on YYYY-MM-DD
                String[] parts = entry.split("—");
                String title = parts[0].replace("•", "").trim();
                double amount = Double.parseDouble(parts[1].split("on")[0].replace("$", "").trim());

                // Aggregate amount by campaign
                boolean found = false;
                for (XYChart.Data<String, Number> data : dataSeries.getData()) {
                    if (data.getXValue().equals(title)) {
                        data.setYValue(data.getYValue().doubleValue() + amount);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    dataSeries.getData().add(new XYChart.Data<>(title, amount));
                }
            } catch (Exception ignored) {
            }
        }

        barChart.getData().add(dataSeries);
        barChart.setMinHeight(300);
        barChart.setLegendVisible(false);

        // Donation text list
        VBox donationBox = new VBox(10);
        donationBox.setPadding(new Insets(10));

        if (donations.isEmpty()) {
            Label noDonations = new Label("No donations made yet.");
            noDonations.setStyle("-fx-text-fill: #999;");
            donationBox.getChildren().add(noDonations);
        } else {
            for (String donation : donations) {
                Label entry = new Label(donation);
                entry.setStyle("-fx-background-color: #f2f2f2; -fx-padding: 10; -fx-border-radius: 4; -fx-background-radius: 4;");
                donationBox.getChildren().add(entry);
            }
        }

        ScrollPane scrollPane = new ScrollPane(donationBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        // Layout
        VBox center = new VBox(20, heading, totalLabel, barChart, scrollPane);
        center.setPadding(new Insets(30));

        BorderPane root = new BorderPane();
        root.setLeft(Sidebar.createSidebar(stage, "donor", userId));
        root.setCenter(center);

        stage.setScene(new Scene(root, 900, 600));
        stage.setTitle("Donation History");
    }
}
