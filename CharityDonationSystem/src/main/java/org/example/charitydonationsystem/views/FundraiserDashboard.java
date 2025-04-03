package org.example.charitydonationsystem.views;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.charitydonationsystem.components.Sidebar;

public class FundraiserDashboard {

    public void show(Stage stage, int userId) {
        Label welcome = new Label("Admin Dashboard");
        welcome.setStyle("-fx-font-size: 18px;");

        VBox content = new VBox(20, welcome);
        content.setStyle("-fx-padding: 30; -fx-alignment: center;");

        BorderPane root = new BorderPane();
        root.setLeft(Sidebar.createSidebar(stage, "fundraiser", userId));
        root.setCenter(content);

        Scene scene = new Scene(root, 800, 500);
        stage.setScene(scene);
        stage.setTitle("Fundraiser Dashboard");
    }
}

