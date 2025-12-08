package com.example.javafx_project;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;

    private UserDAO userDAO = new UserDAO();

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs.");
            return;
        }
        // Appeler la méthode login de DatabaseConnection
        User user = userDAO.login(email, password);
        if (user != null) {
            // Login réussi, rediriger selon le rôle
            if ("ADMIN".equals(user.getRole())) {
                openAdminView(user);
            } else if ("GEST".equals(user.getRole())) {
                openGestView(user);
            }
        } else {
            showAlert("Erreur", "Email ou mot de passe incorrect.");
        }
    }

    private void openAdminView(User user) {
        try {
            // Charge une nouvelle vue pour ADMIN (crée AdminView.fxml si nécessaire)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminView.fxml"));
            Parent root = loader.load();
            // Passe l'utilisateur au contrôleur suivant si besoin
            // AdminController controller = loader.getController();
            // controller.setUser(user);
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Interface Admin - " + user.getNom());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger l'interface Admin.");
        }
    }

    private void openGestView(User user) {
        try {
            // Charge une nouvelle vue pour GEST (crée GestView.fxml si nécessaire)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GestView.fxml"));
            Parent root = loader.load();
            // Passe l'utilisateur au contrôleur suivant si besoin
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Interface Gestionnaire - " + user.getNom());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger l'interface Gestionnaire.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();  // Affiche l'alerte et attend que l'utilisateur la ferme
    }
}
