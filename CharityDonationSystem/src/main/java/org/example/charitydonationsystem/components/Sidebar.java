package org.example.charitydonationsystem.components;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.charitydonationsystem.views.*;

public class Sidebar {

    public static VBox createSidebar(Stage stage, String role, int userId) {
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #f0f0f0; -fx-min-width: 200px;");

        Button dashboardBtn = new Button("Dashboard");
        Button profileBtn = new Button("Profile");
        Button logoutBtn = new Button("Logout");

        // Handle navigation
        dashboardBtn.setOnAction(e -> {
            switch (role) {
                case "admin" -> new AdminDashboard().show(stage, userId);
                case "donor" -> new DonorDashboard().show(stage, userId);
                case "fundraiser" -> new FundraiserDashboard().show(stage, userId);
            }
        });

        profileBtn.setOnAction(e -> {
            new ProfileView().show(stage, role, userId);
        });

        logoutBtn.setOnAction(e -> {
            new LoginView().start(stage);
        });

        sidebar.getChildren().addAll(dashboardBtn, profileBtn);

        // Role-specific options
        if (role.equals("admin")) {
            Button manageUsersBtn = new Button("Manage Users");
            Button reportsBtn = new Button("Reports");
            reportsBtn.setOnAction(e -> new AdminDonationReports().show(stage, userId));
            sidebar.getChildren().addAll(manageUsersBtn, reportsBtn);
            Button campaignOverviewBtn = new Button("Campaigns Overview");
            campaignOverviewBtn.setOnAction(e -> new AdminCampaignView().show(stage, userId));
            sidebar.getChildren().add(campaignOverviewBtn);

        } else if (role.equals("fundraiser")) {
            Button manageCampaignsBtn = new Button("Manage Campaigns");
            manageCampaignsBtn.setOnAction(e -> new FundraiserCampaignManager().show(stage, userId));
            sidebar.getChildren().add(manageCampaignsBtn);

        } else if (role.equals("donor")) {
            Button browseCampaignsBtn = new Button("Browse Campaigns");
            Button donationHistoryBtn = new Button("Donation History");
            browseCampaignsBtn.setOnAction(e -> new DonorCampaignBrowser().show(stage, userId));
            donationHistoryBtn.setOnAction(e -> new DonorDonationHistory().show(stage, userId));
            sidebar.getChildren().addAll(browseCampaignsBtn, donationHistoryBtn);
        }

        sidebar.getChildren().add(logoutBtn);

        return sidebar;
    }
}

