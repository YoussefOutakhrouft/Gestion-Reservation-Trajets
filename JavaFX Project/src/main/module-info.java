module com.example.javafx_project {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.javafx_project to javafx.fxml;
    exports com.example.javafx_project;
}