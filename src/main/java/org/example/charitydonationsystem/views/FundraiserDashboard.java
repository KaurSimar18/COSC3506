package org.example.charitydonationsystem.views;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.charitydonationsystem.components.Sidebar;
import org.example.charitydonationsystem.dao.CampaignDAO;
import org.example.charitydonationsystem.models.Campaign;
import org.example.charitydonationsystem.utils.CSVUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Fundraiser Dashboard view — shows donation report and campaign stats.
 */
public class FundraiserDashboard {

    public void show(Stage stage, int userId) {
        Label welcome = new Label("Fundraiser Dashboard");
        welcome.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // CSV export
        Button exportBtn = new Button("Export My Campaigns as CSV");
        Label exportStatus = new Label();

        exportBtn.setOnAction(e -> {
            List<Campaign> myCampaigns = CampaignDAO.getCampaignsByFundraiser(userId);

            List<String[]> rows = new ArrayList<>();
            for (Campaign c : myCampaigns) {
                rows.add(new String[]{
                        c.getTitle(),
                        String.valueOf(c.getGoalAmount()),
                        String.valueOf(c.getAmountRaised()),
                        c.getStatus()
                });
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Campaign Report");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            fileChooser.setInitialFileName("my_campaigns.csv");

            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                CSVUtil.writeCSV(file.getAbsolutePath(), new String[]{"Title", "Goal", "Raised", "Status"}, rows);
                exportStatus.setText("Report saved to: " + file.getAbsolutePath());
            }
        });

        // Campaign donation chart
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Campaign");
        yAxis.setLabel("Amount Raised");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Donations Raised by Your Campaigns");
        chart.setLegendVisible(false);
        chart.setMinHeight(300);

        XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();
        List<Campaign> campaigns = CampaignDAO.getCampaignsByFundraiser(userId);
        for (Campaign c : campaigns) {
            dataSeries.getData().add(new XYChart.Data<>(c.getTitle(), c.getAmountRaised()));
        }
        chart.getData().add(dataSeries);

        // Layout
        VBox content = new VBox(20, welcome, chart, exportBtn, exportStatus);
        content.setPadding(new Insets(30));

        BorderPane root = new BorderPane();
        root.setLeft(Sidebar.createSidebar(stage, "fundraiser", userId));
        root.setCenter(content);

        Scene scene = new Scene(root, 900, 600);
        stage.setScene(scene);
        stage.setTitle("Fundraiser Dashboard");
    }
}
