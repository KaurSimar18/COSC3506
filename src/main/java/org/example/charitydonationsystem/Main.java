package org.example.charitydonationsystem;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.charitydonationsystem.views.LoginView;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        DBUtil.initializeDatabase();
        new LoginView().start(stage);
    }
}
