package org.example.charitydonationsystem.views;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.charitydonationsystem.components.Sidebar;
import org.example.charitydonationsystem.dao.DonationDAO;
import org.example.charitydonationsystem.utils.CSVUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AdminDonationReports {

    public void show(Stage stage, int userId) {
        Label heading = new Label("Donation Reports");
        heading.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        double total = DonationDAO.getTotalDonations();
        Label totalLabel = new Label("Total Donations Across All Campaigns: $" + total);
        totalLabel.setStyle("-fx-font-size: 14px; -fx-font-style: italic;");

        VBox statsBox = new VBox(10);
        List<String> stats = DonationDAO.getDonationStatsByCampaign();
        for (String s : stats) {
            statsBox.getChildren().add(new Label(s));
        }

        VBox topBox = new VBox(5);
        topBox.getChildren().add(new Label("Top 3 Campaigns:"));
        List<String> top = DonationDAO.getTopCampaigns(3);
        for (String s : top) {
            topBox.getChildren().add(new Label(s));
        }

        // 🔽 Export CSV Button
        Button exportBtn = new Button("Export Report as CSV");
        Label exportStatus = new Label();

        exportBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Donation Report");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            fileChooser.setInitialFileName("donation_report.csv");

            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                List<String[]> rows = new ArrayList<>();

                for (String line : stats) {
                    // crude split just for this display format
                    String[] parts = line.replace("• ", "").split("\\|");
                    String campaign = parts[0].trim();
                    String count = parts[1].replace("Donations:", "").trim();
                    String totalRaised = parts[2].replace("Total Raised:", "").replace("$", "").trim();
                    rows.add(new String[]{campaign, count, totalRaised});
                }

                CSVUtil.writeCSV(file.getAbsolutePath(), new String[]{"Campaign", "Donations", "Total Raised"}, rows);
                exportStatus.setText("Report saved to: " + file.getAbsolutePath());
            }
        });

        VBox content = new VBox(15, heading, totalLabel, new Label("Donations per Campaign:"), statsBox, topBox, exportBtn, exportStatus);
        content.setPadding(new Insets(30));

        BorderPane root = new BorderPane();
        root.setLeft(Sidebar.createSidebar(stage, "admin", userId));
        root.setCenter(content);

        stage.setScene(new Scene(root, 900, 600));
        stage.setTitle("Donation Reports");
    }
}
