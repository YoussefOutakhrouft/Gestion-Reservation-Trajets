package com.example.javafx_project;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class GestController {
    @FXML
    private Button mesTachesBtn;
    @FXML
    private Button logoutBtn;
    @FXML
    private Label contentLabel;
    @FXML
    private void showMesTaches() {
        contentLabel.setText("Mes Tâches - Ici, affiche les tâches assignées à l'utilisateur.");
        // Tu peux étendre pour charger un FXML séparé ou une TableView ici
    }
    @FXML
    private void logout() {
        try {
            // Redirige vers la vue de login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) logoutBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
