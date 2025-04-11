package org.example.charitydonationsystem.components;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.charitydonationsystem.views.*;

/**
 * Sidebar is a static vertical menu that appears on the left side.
 * It shows buttons based on the user's role and navigates to the appropriate views.
 */
public class Sidebar {

    public static VBox createSidebar(Stage stage, String role, int userId) {
        VBox sidebar = new VBox(12);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("""
                    -fx-background-color: #2c3e50;
                    -fx-min-width: 200px;
                    -fx-pref-width: 200px;
                    -fx-max-width: 200px;
                """);

        // Common buttons
        Button dashboardBtn = createNavButton("Dashboard", () -> {
            switch (role) {
                case "admin" -> new AdminDashboard().show(stage, userId);
                case "donor" -> new DonorDashboard().show(stage, userId);
                case "fundraiser" -> new FundraiserDashboard().show(stage, userId);
            }
        });

        Button profileBtn = createNavButton("Profile", () -> new ProfileView().show(stage, role, userId));

        Button logoutBtn = createNavButton("Logout", () -> new LoginView().start(stage));

        sidebar.getChildren().addAll(dashboardBtn, profileBtn);

        // Role-specific options
        switch (role) {
            case "admin" -> {
                Button reportsBtn = createNavButton("Reports", () -> new AdminDonationReports().show(stage, userId));
                Button campaignOverviewBtn = createNavButton("Campaigns Overview", () -> new AdminCampaignView().show(stage, userId));
                Button manageUsersBtn = createNavButton("Manage Users", () -> new AdminUserManagerView().show(stage, userId));
                sidebar.getChildren().addAll(manageUsersBtn, reportsBtn, campaignOverviewBtn);
            }
            case "fundraiser" -> {
                Button manageCampaignsBtn = createNavButton("Manage Campaigns", () -> new FundraiserCampaignManager().show(stage, userId));
                Button notificationsBtn = createNavButton("Notifications", () -> new FundraiserNotificationView().show(stage, userId));
                sidebar.getChildren().addAll(manageCampaignsBtn, notificationsBtn);
            }
            case "donor" -> {
                Button browseCampaignsBtn = createNavButton("Browse Campaigns", () -> new DonorCampaignBrowser().show(stage, userId));
                Button donationHistoryBtn = createNavButton("Donation History", () -> new DonorDonationHistory().show(stage, userId));
                Button notificationsBtn = createNavButton("Notifications", () -> new DonorNotificationView().show(stage, userId));
                sidebar.getChildren().addAll(browseCampaignsBtn, donationHistoryBtn, notificationsBtn);
            }
        }

        sidebar.getChildren().add(logoutBtn);
        return sidebar;
    }

    /**
     * Creates a styled button with hover effect and action handler.
     */
    private static Button createNavButton(String text, Runnable onClick) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("""
                    -fx-background-color: #34495e;
                    -fx-text-fill: white;
                    -fx-font-size: 14px;
                    -fx-padding: 10 15 10 15;
                    -fx-border-radius: 4;
                    -fx-background-radius: 4;
                """);

        // Proper hover effect (persistent and clean)
        btn.setOnMouseEntered(e -> btn.setStyle("""
                    -fx-background-color: #3b5998;
                    -fx-text-fill: white;
                    -fx-font-size: 14px;
                    -fx-padding: 10 15 10 15;
                    -fx-border-radius: 4;
                    -fx-background-radius: 4;
                """));
        btn.setOnMouseExited(e -> btn.setStyle("""
                    -fx-background-color: #34495e;
                    -fx-text-fill: white;
                    -fx-font-size: 14px;
                    -fx-padding: 10 15 10 15;
                    -fx-border-radius: 4;
                    -fx-background-radius: 4;
                """));

        btn.setOnAction(e -> onClick.run());
        return btn;
    }
}
