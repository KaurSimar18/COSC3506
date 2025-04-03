package org.example.charitydonationsystem.views;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.charitydonationsystem.components.Sidebar;
import org.example.charitydonationsystem.dao.CampaignDAO;
import org.example.charitydonationsystem.models.Campaign;

import java.time.LocalDate;
import java.util.List;

public class FundraiserCampaignManager {

    public void show(Stage stage, int userId) {
        Label heading = new Label("Manage Campaigns");
        heading.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        VBox campaignList = new VBox(10);
        refreshCampaigns(campaignList, userId);

        // Form to add campaign
        TextField titleField = new TextField();
        titleField.setPromptText("Campaign Title");

        TextField goalField = new TextField();
        goalField.setPromptText("Goal Amount");

        TextField descField = new TextField();
        descField.setPromptText("Description");

        TextField endDateField = new TextField();
        endDateField.setPromptText("End Date (YYYY-MM-DD)");

        Button addBtn = new Button("Add Campaign");
        Label status = new Label();

        addBtn.setOnAction(e -> {
            try {
                String title = titleField.getText().trim();
                String desc = descField.getText().trim();
                double goal = Double.parseDouble(goalField.getText().trim());
                String endDate = endDateField.getText().trim();
                String today = LocalDate.now().toString();

                Campaign c = new Campaign(0, userId, title, desc, goal, 0, today, endDate, "active");
                CampaignDAO.addCampaign(c);

                status.setText("Campaign added!");
                refreshCampaigns(campaignList, userId);
            } catch (Exception ex) {
                ex.printStackTrace();
                status.setText("Error adding campaign.");
            }
        });

        VBox form = new VBox(10, new Label("Create New Campaign:"), titleField, goalField, descField, endDateField, addBtn, status);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-border-color: gray; -fx-padding: 10;");

        VBox centerContent = new VBox(20, heading, form, new Label("Your Campaigns:"), campaignList);
        centerContent.setPadding(new Insets(30));

        BorderPane root = new BorderPane();
        root.setLeft(Sidebar.createSidebar(stage, "fundraiser", userId));
        root.setCenter(centerContent);

        stage.setScene(new Scene(root, 900, 600));
        stage.setTitle("Fundraiser Campaign Manager");
    }

    private void showEditPopup(Campaign c, int userId) {
        Stage popup = new Stage();
        popup.setTitle("Edit Campaign");

        TextField titleField = new TextField(c.getTitle());
        TextField goalField = new TextField(String.valueOf(c.getGoalAmount()));
        TextField descField = new TextField(c.getDescription());
        TextField endDateField = new TextField(c.getEndDate());
        TextField statusField = new TextField(c.getStatus()); // optional: dropdown instead

        Button saveBtn = new Button("Save");
        Label result = new Label();

        saveBtn.setOnAction(e -> {
            try {
                c.setTitle(titleField.getText().trim());
                c.setGoalAmount(Double.parseDouble(goalField.getText().trim()));
                c.setDescription(descField.getText().trim());
                c.setEndDate(endDateField.getText().trim());
                c.setStatus(statusField.getText().trim());

                CampaignDAO.updateCampaign(c);
                result.setText("Updated successfully!");
                popup.close(); // Close popup after save

                // Refresh main list
                show(popup.getOwner() instanceof Stage ? (Stage) popup.getOwner() : null, userId);
            } catch (Exception ex) {
                ex.printStackTrace();
                result.setText("Update failed.");
            }
        });

        VBox layout = new VBox(10,
                new Label("Title"), titleField,
                new Label("Goal Amount"), goalField,
                new Label("Description"), descField,
                new Label("End Date"), endDateField,
                new Label("Status"), statusField,
                saveBtn, result
        );
        layout.setPadding(new Insets(20));

        popup.setScene(new Scene(layout, 300, 400));
        popup.show();
    }

    private void refreshCampaigns(VBox campaignList, int userId) {
        campaignList.getChildren().clear();
        List<Campaign> campaigns = CampaignDAO.getCampaignsByFundraiser(userId);

        for (Campaign c : campaigns) {
            Label label = new Label(c.getTitle() + " | Goal: $" + c.getGoalAmount() + " | Raised: $" + c.getAmountRaised());

            Button editBtn = new Button("Edit");
            Button deleteBtn = new Button("Delete");

            HBox campaignBox = new HBox(10, label, editBtn, deleteBtn);
            campaignBox.setPadding(new Insets(5));

            // Edit Handler
            editBtn.setOnAction(e -> showEditPopup(c, userId));

            // Delete Handler
            deleteBtn.setOnAction(e -> {
                CampaignDAO.deleteCampaign(c.getId());
                refreshCampaigns(campaignList, userId);
            });

            campaignList.getChildren().add(campaignBox);
        }
    }

}

