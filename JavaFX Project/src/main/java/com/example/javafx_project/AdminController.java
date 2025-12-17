package com.example.javafx_project;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public class AdminController {
    @FXML
    private Button gestionUtilisateursBtn;
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

    @FXML private Region indicatorUsers;
    @FXML private Region indicatorTrajets;
    @FXML private Region indicatorVehicules;
    @FXML private Region indicatorListeTrajets;


    @FXML
    public void initialize() {
        setSelectedButton(gestionUtilisateursBtn);
    }
    private void setSelectedButton(Button selected) {

        Button[] buttons = {
                gestionUtilisateursBtn,
                gestionTrajetsBtn,
                gestionVehiculesBtn,
                listeTrajetsBtn
        };

        for (Button btn : buttons) {
            btn.getStyleClass().remove("selected");
        }

        selected.getStyleClass().add("selected");
    }


    @FXML
    private void showGestionUtilisateurs() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CreateUserView.fxml"));
            Parent createUserView = loader.load();
            rootPane.setCenter(createUserView);  // Définit le contenu central
            setSelectedButton(gestionUtilisateursBtn);
            //contentLabel.setText("Gestion des Utilisateurs");
        } catch (Exception e) {
            e.printStackTrace();
            contentLabel.setText("Erreur lors du chargement de la vue Gestion Utilisateurs.");
        }
    }
    @FXML
    private void showGestionTrajets() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("TrajetView.fxml"));
            Parent trajetView = loader.load();
            rootPane.setCenter(trajetView);
            setSelectedButton(gestionTrajetsBtn);
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
            setSelectedButton(gestionVehiculesBtn);
            //contentLabel.setText("Gestion des Véhicules");
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
            setSelectedButton(listeTrajetsBtn);
            //contentLabel.setText("Liste des Trajets");
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
