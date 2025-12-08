package com.example.javafx_project;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class AdminController {
    @FXML
    private Button gestionUtilisateursBtn;
    @FXML
    private Button gestionTachesBtn;
    @FXML
    private Button gestionTrajetsBtn;
    @FXML
    private Button gestionVehiculesBtn;
    @FXML
    private Button listeTrajetsBtn;
    @FXML
    private Button logoutBtn;
    @FXML
    private Label contentLabel;
    @FXML
    private BorderPane rootPane;
    @FXML
    private void showGestionUtilisateurs() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CreateUserView.fxml"));
            Parent createUserView = loader.load();
            rootPane.setCenter(createUserView);  // Définit le contenu central
        } catch (Exception e) {
            e.printStackTrace();
            contentLabel.setText("Erreur lors du chargement de la vue Gestion Utilisateurs.");
        }
    }
    @FXML
    private void showGestionTaches() {
        contentLabel.setText("Gestion des Tâches - Ici, affiche une liste/table des tâches.");
        // Tu peux étendre pour charger un FXML séparé ou une TableView ici
    }
    @FXML
    private void showGestionTrajets() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("TrajetView.fxml"));
            Parent trajetView = loader.load();
            rootPane.setCenter(trajetView);
        } catch (Exception e) {
            e.printStackTrace();
            contentLabel.setText("Erreur lors du chargement de la vue Gestion Trajets.");
        }
    }
    @FXML
    private void showGestionVehicules() {  // Nouvelle méthode
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("VehiculeView.fxml"));
            Parent vehiculeView = loader.load();
            rootPane.setCenter(vehiculeView);
        } catch (Exception e) {
            e.printStackTrace();
            contentLabel.setText("Erreur lors du chargement de la vue Gestion Véhicules.");
        }
    }
    @FXML
    private void showListeTrajets() {  // Nouvelle méthode
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ListeTrajetsView.fxml"));
            Parent listeTrajetsView = loader.load();
            rootPane.setCenter(listeTrajetsView);
        } catch (Exception e) {
            e.printStackTrace();
            contentLabel.setText("Erreur lors du chargement de la vue Liste Trajets.");
        }
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
