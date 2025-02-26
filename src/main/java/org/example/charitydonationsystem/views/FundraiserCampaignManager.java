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

/**
 * FundraiserCampaignManager allows fundraisers to create, edit, and delete their campaigns.
 * It shows a list of the user's campaigns and a form to create new ones.
 */
public class FundraiserCampaignManager {

    /**
     * Main UI method to display the campaign manager screen.
     *
     * @param stage  JavaFX stage
     * @param userId the current fundraiser's ID
     */
    public void show(Stage stage, int userId) {
        Label heading = new Label("Manage Your Campaigns");
        heading.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        VBox campaignList = new VBox(10);
        refreshCampaigns(campaignList, userId); // fill list of existing campaigns

        // ========== Form to create a new campaign ==========
        TextField titleField = new TextField();
        titleField.setPromptText("Campaign Title");

        TextField goalField = new TextField();
        goalField.setPromptText("Goal Amount");

        TextField descField = new TextField();
        descField.setPromptText("Short Description");

        TextField endDateField = new TextField();
        endDateField.setPromptText("End Date (YYYY-MM-DD)");

        Button addBtn = new Button("Add Campaign");
        Label status = new Label();
        status.setStyle("-fx-text-fill: green;");

        addBtn.setOnAction(e -> {
            try {
                String title = titleField.getText().trim();
                String desc = descField.getText().trim();
                double goal = Double.parseDouble(goalField.getText().trim());
                String endDate = endDateField.getText().trim();
                String today = LocalDate.now().toString();

                if (title.isEmpty() || desc.isEmpty() || endDate.isEmpty()) {
                    status.setText("Please fill in all fields.");
                    status.setStyle("-fx-text-fill: red;");
                    return;
                }

                Campaign c = new Campaign(0, userId, title, desc, goal, 0, today, endDate, "active");
                CampaignDAO.addCampaign(c);

                status.setText("Campaign added successfully!");
                status.setStyle("-fx-text-fill: green;");
                refreshCampaigns(campaignList, userId);

                // Clear form
                titleField.clear();
                goalField.clear();
                descField.clear();
                endDateField.clear();
            } catch (Exception ex) {
                ex.printStackTrace();
                status.setText("Error: Please check input values.");
                status.setStyle("-fx-text-fill: red;");
            }
        });

        VBox form = new VBox(10,
                new Label("Create New Campaign:"),
                titleField, goalField, descField, endDateField,
                addBtn, status
        );
        form.setPadding(new Insets(20));
        form.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 4; -fx-padding: 15; -fx-background-color: #f9f9f9;");

        // Layout combining heading, form, and existing campaigns
        VBox centerContent = new VBox(25, heading, form, new Label("Your Campaigns:"), campaignList);
        centerContent.setPadding(new Insets(30));

        BorderPane root = new BorderPane();
        root.setLeft(Sidebar.createSidebar(stage, "fundraiser", userId));
        root.setCenter(centerContent);

        stage.setScene(new Scene(root, 900, 600));
        stage.setTitle("Fundraiser Campaign Manager");
    }

    /**
     * Displays a popup window for editing an existing campaign.
     */
    private void showEditPopup(Campaign c, int userId) {
        Stage popup = new Stage();
        popup.setTitle("Edit Campaign");

        TextField titleField = new TextField(c.getTitle());
        TextField goalField = new TextField(String.valueOf(c.getGoalAmount()));
        TextField descField = new TextField(c.getDescription());
        TextField endDateField = new TextField(c.getEndDate());
        TextField statusField = new TextField(c.getStatus());

        Button saveBtn = new Button("Save Changes");
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
                popup.close();

                // Refresh the main view
                show(popup.getOwner() instanceof Stage ? (Stage) popup.getOwner() : null, userId);

            } catch (Exception ex) {
                ex.printStackTrace();
                result.setText("Update failed. Check values.");
            }
        });

        VBox layout = new VBox(10,
                new Label("Title:"), titleField,
                new Label("Goal Amount:"), goalField,
                new Label("Description:"), descField,
                new Label("End Date:"), endDateField,
                new Label("Status:"), statusField,
                saveBtn, result
        );
        layout.setPadding(new Insets(20));

        popup.setScene(new Scene(layout, 300, 420));
        popup.show();
    }

    /**
     * Loads and displays all campaigns created by the current fundraiser.
     */
    private void refreshCampaigns(VBox campaignList, int userId) {
        campaignList.getChildren().clear();
        List<Campaign> campaigns = CampaignDAO.getCampaignsByFundraiser(userId);

        for (Campaign c : campaigns) {
            Label label = new Label(c.getTitle() + " | Goal: $" + c.getGoalAmount() + " | Raised: $" + c.getAmountRaised());
            label.setStyle("-fx-font-size: 13px;");

            Button editBtn = new Button("Edit");
            Button deleteBtn = new Button("Delete");

            editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
            deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

            // Action Buttons
            editBtn.setOnAction(e -> showEditPopup(c, userId));
            deleteBtn.setOnAction(e -> {
                CampaignDAO.deleteCampaign(c.getId());
                refreshCampaigns(campaignList, userId);
            });

            HBox campaignBox = new HBox(10, label, editBtn, deleteBtn);
            campaignBox.setPadding(new Insets(8));
            campaignBox.setStyle("-fx-border-color: #dddddd; -fx-background-color: #fcfcfc; -fx-border-radius: 4; -fx-background-radius: 4;");

            campaignList.getChildren().add(campaignBox);
        }
    }
}
