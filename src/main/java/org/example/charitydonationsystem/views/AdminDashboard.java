package org.example.charitydonationsystem.views;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.charitydonationsystem.components.Sidebar;
import org.example.charitydonationsystem.dao.DonationDAO;

import java.util.List;

/**
 * Admin dashboard view with donation visualizations.
 */
public class AdminDashboard {

    public void show(Stage stage, int userId) {
        Label heading = new Label("Admin Dashboard");
        heading.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Chart 1: BarChart for campaign totals
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Campaign");
        yAxis.setLabel("Total Raised ($)");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Total Donations by Campaign");
        barChart.setLegendVisible(false);
        barChart.setMinHeight(300);

        XYChart.Series<String, Number> barSeries = new XYChart.Series<>();

        // Chart 2: PieChart for donation distribution
        PieChart pieChart = new PieChart();
        pieChart.setTitle("Donation Distribution");

        List<String> stats = DonationDAO.getDonationStatsByCampaign();
        for (String line : stats) {
            try {
                // Format: • Campaign Title | Donations: X | Total Raised: $Y
                String[] parts = line.replace("•", "").split("\\|");
                String title = parts[0].trim();
                String amountStr = parts[2].replace("Total Raised:", "").replace("$", "").trim();
                double amount = Double.parseDouble(amountStr);

                // Add to bar and pie charts
                barSeries.getData().add(new XYChart.Data<>(title, amount));
                pieChart.getData().add(new PieChart.Data(title, amount));
            } catch (Exception ignored) {
            }
        }

        barChart.getData().add(barSeries);

        VBox content = new VBox(20, heading, barChart, pieChart);
        content.setPadding(new Insets(30));

        BorderPane root = new BorderPane();
        root.setLeft(Sidebar.createSidebar(stage, "admin", userId));
        root.setCenter(content);

        Scene scene = new Scene(root, 900, 700);
        stage.setScene(scene);
        stage.setTitle("Admin Dashboard");
    }
}
