module org.example.charitydonationsystem {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.sql;

    opens org.example.charitydonationsystem to javafx.fxml;
    exports org.example.charitydonationsystem;
    exports org.example.charitydonationsystem.views;
    opens org.example.charitydonationsystem.views to javafx.fxml;
}