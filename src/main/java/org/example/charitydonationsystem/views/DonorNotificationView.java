package org.example.charitydonationsystem.views;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.charitydonationsystem.components.Sidebar;
import org.example.charitydonationsystem.dao.NotificationDAO;

import java.util.List;

public class DonorNotificationView {

    public void show(Stage stage, int userId) {
        Label heading = new Label("Notifications");
        heading.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        VBox notificationBox = new VBox(10);
        notificationBox.setPadding(new Insets(10));

        List<String> notifications = NotificationDAO.getNotificationsByUser(userId);
        if (notifications.isEmpty()) {
            notificationBox.getChildren().add(new Label("No notifications."));
        } else {
            for (String msg : notifications) {
                Label label = new Label(msg);
                label.setStyle("-fx-background-color: #f9f9f9; -fx-padding: 10; -fx-border-radius: 4;");
                notificationBox.getChildren().add(label);
            }
        }

        ScrollPane scrollPane = new ScrollPane(notificationBox);
        scrollPane.setFitToWidth(true);

        VBox center = new VBox(20, heading, scrollPane);
        center.setPadding(new Insets(30));

        BorderPane root = new BorderPane();
        root.setLeft(Sidebar.createSidebar(stage, "donor", userId));
        root.setCenter(center);

        stage.setScene(new Scene(root, 800, 600));
        stage.setTitle("Your Notifications");
    }
}

