package com.example.javafx_project;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
        // Récupérer et nettoyer les valeurs
        String numeroMatricule = numeroMatriculeField.getText().trim();
        String marque = marqueField.getText().trim();
        String nombrePlacesStr = nombrePlacesField.getText().trim();

        // Validation des champs vides
        if (numeroMatricule.isEmpty() || marque.isEmpty() || nombrePlacesStr.isEmpty()) {
            showAlert(
                    "Champs manquants",
                    "Veuillez remplir tous les champs obligatoires (*)",
                    Alert.AlertType.WARNING
            );
            return;
        }

        // Validation du numéro de matricule
        if (numeroMatricule.length() < 3) {
            showAlert(
                    "Matricule invalide",
                    "Le numéro de matricule doit contenir au moins 3 caractères.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        // Validation de la marque
        if (marque.length() < 2) {
            showAlert(
                    "Marque invalide",
                    "La marque doit contenir au moins 2 caractères.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        // Validation du nombre de places
        try {
            int nombrePlaces = Integer.parseInt(nombrePlacesStr);

            if (nombrePlaces <= 0) {
                showAlert(
                        "Nombre de places invalide",
                        "Le nombre de places doit être un entier positif (supérieur à 0).",
                        Alert.AlertType.WARNING
                );
                return;
            }

            if (nombrePlaces > 100) {
                showAlert(
                        "Nombre de places invalide",
                        "Le nombre de places ne peut pas dépasser 100.",
                        Alert.AlertType.WARNING
                );
                return;
            }

            // Vérifier si le matricule existe déjà
            VehiculeDAO vehiculeDAO = new VehiculeDAO();
            if (isMatriculeExists(numeroMatricule)) {
                showAlert(
                        "Matricule existant",
                        "Un véhicule avec ce numéro de matricule existe déjà.\nVeuillez utiliser un numéro différent.",
                        Alert.AlertType.ERROR
                );
                return;
            }

            // Créer et sauvegarder le véhicule
            Vehicule vehicule = new Vehicule(numeroMatricule, marque, nombrePlaces);
            vehiculeDAO.insertVehicule(vehicule);

            // Afficher le message de succès avec les détails
            showAlert(
                    "Véhicule créé avec succès !",
                    "Le véhicule a été ajouté à votre flotte :\n\n" +
                            "📋 Matricule : " + numeroMatricule + "\n" +
                            "🏷️ Marque : " + marque + "\n" +
                            "🪑 Places : " + nombrePlaces,
                    Alert.AlertType.INFORMATION
            );

            clearForm();

            // Fermer la fenêtre après création
            Stage stage = (Stage) saveBtn.getScene().getWindow();
            stage.close();

        } catch (NumberFormatException e) {
            showAlert(
                    "Format invalide",
                    "Le nombre de places doit être un nombre entier valide.\n\n" +
                            "Exemples valides : 20, 50, 75",
                    Alert.AlertType.ERROR
            );
        } catch (Exception e) {
            showAlert(
                    "Erreur",
                    "Une erreur est survenue lors de la création du véhicule.\n\n" +
                            "Détails : " + e.getMessage(),
                    Alert.AlertType.ERROR
            );
            e.printStackTrace();
        }
    }

    private boolean isMatriculeExists(String matricule) {
        VehiculeDAO vehiculeDAO = new VehiculeDAO();
        try {
            // Vérifier si un véhicule avec ce matricule existe déjà
            for (Vehicule v : vehiculeDAO.getAllVehicules()) {
                if (v.getNumeroMatricule().equalsIgnoreCase(matricule)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @FXML
    private void cancel() {
        // Vérifier s'il y a des données non sauvegardées
        if (!numeroMatriculeField.getText().trim().isEmpty() ||
                !marqueField.getText().trim().isEmpty() ||
                !nombrePlacesField.getText().trim().isEmpty()) {

            // Demander confirmation
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation");
            confirmation.setHeaderText("Annuler la création ?");
            confirmation.setContentText(
                    "Vous avez des données non sauvegardées.\n\n" +
                            "Êtes-vous sûr de vouloir annuler ?"
            );

            confirmation.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    closeWindow();
                }
            });
        } else {
            closeWindow();
        }
    }

    private void closeWindow() {
        clearForm();
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Personnaliser l'icône selon le type
        switch (type) {
            case INFORMATION:
                alert.setHeaderText("✅ " + title);
                break;
            case WARNING:
                alert.setHeaderText("⚠️ " + title);
                break;
            case ERROR:
                alert.setHeaderText("❌ " + title);
                break;
        }

        alert.showAndWait();
    }

    private void clearForm() {
        numeroMatriculeField.clear();
        marqueField.clear();
        nombrePlacesField.clear();
    }
}