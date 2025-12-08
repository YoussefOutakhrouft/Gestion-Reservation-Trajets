package com.example.javafx_project;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class VehiculeController {
    @FXML
    private TextField numeroMatriculeField;
    @FXML
    private TextField marqueField;
    @FXML
    private TextField nombrePlacesField;
    @FXML
    private Button saveBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private void saveVehicule() {
        // Valider les champs
        if (numeroMatriculeField.getText().isEmpty() || marqueField.getText().isEmpty() || nombrePlacesField.getText().isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs.");
            return;
        }
        try {
            int nombrePlaces = Integer.parseInt(nombrePlacesField.getText());
            if (nombrePlaces <= 0) {
                showAlert("Erreur", "Le nombre de places doit être un entier positif.");
                return;
            }
            // Créer et sauvegarder le véhicule
            Vehicule vehicule = new Vehicule(numeroMatriculeField.getText(), marqueField.getText(), nombrePlaces);
            VehiculeDAO vehiculeDAO = new VehiculeDAO();
            vehiculeDAO.insertVehicule(vehicule);
            showAlert("Succès", "Véhicule créé avec succès !");
            clearForm();

        } catch (NumberFormatException e) {
            showAlert("Erreur", "Le nombre de places doit être un nombre entier.");
        }
    }

    @FXML
    private void cancel() {
        // Retourner à la vue admin
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) cancelBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void clearForm() {
        numeroMatriculeField.clear();
        marqueField.clear();
        nombrePlacesField.clear();
    }
}
