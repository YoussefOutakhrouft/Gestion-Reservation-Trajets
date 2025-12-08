package com.example.javafx_project;

import com.example.javafx_project.TrajetDAO;
import com.example.javafx_project.Trajet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ListeTrajetsController {

    @FXML
    private TableView<Trajet> trajetsTable;
    @FXML
    private TableColumn<Trajet, Integer> idColumn;
    @FXML
    private TableColumn<Trajet, LocalDate> dateColumn;
    @FXML
    private TableColumn<Trajet, String> heureDepartColumn;
    @FXML
    private TableColumn<Trajet, String> villeDepartColumn;
    @FXML
    private TableColumn<Trajet, String> villeArriveeColumn;
    @FXML
    private TableColumn<Trajet, String> vehiculeColumn;
    @FXML
    private TableColumn<Trajet, Integer> placesRestantesColumn;
    @FXML
    private Button modifierBtn;
    @FXML
    private Button supprimerBtn;
    @FXML
    private Button refreshBtn;

    private ObservableList<Trajet> trajets = FXCollections.observableArrayList();
    private TrajetDAO trajetDAO = new TrajetDAO();

    @FXML
    public void initialize() {
        // Configurer les colonnes de la TableView
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateTrajet"));
        heureDepartColumn.setCellValueFactory(new PropertyValueFactory<>("heureDepart"));
        villeDepartColumn.setCellValueFactory(new PropertyValueFactory<>("villeDepart"));
        villeArriveeColumn.setCellValueFactory(new PropertyValueFactory<>("villeArrivee"));
        vehiculeColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getVehicule().getNumeroMatricule()));
        placesRestantesColumn.setCellValueFactory(new PropertyValueFactory<>("placesRestantes"));

        // Charger et afficher les trajets
        refreshList();
    }

    @FXML
    private void refreshList() {
        trajets.clear();
        List<Trajet> allTrajets = trajetDAO.getAllTrajets();

        // Supprimer automatiquement les trajets dépassés
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");  // Changé à "H:mm" pour accepter 1 ou 2 chiffres pour les heures
        allTrajets.removeIf(trajet -> {
            try {
                LocalTime heureDepart = LocalTime.parse(trajet.getHeureDepart(), formatter);
                LocalDateTime trajetDateTime = LocalDateTime.of(trajet.getDateTrajet(), heureDepart);
                if (trajetDateTime.isBefore(now)) {
                    trajetDAO.deleteTrajet(trajet.getId());
                    return true;
                }
            } catch (Exception e) {
                System.err.println("Erreur de parsing de l'heure pour le trajet ID " + trajet.getId() + ": " + e.getMessage());
            }
            return false;
        });

        trajets.addAll(allTrajets);
        trajetsTable.setItems(trajets);
    }

    @FXML
    private void modifierTrajet() {
        Trajet selectedTrajet = trajetsTable.getSelectionModel().getSelectedItem();
        if (selectedTrajet == null) {
            showAlert("Erreur", "Veuillez sélectionner un trajet à modifier.");
            return;
        }
        // Passer le trajet à TrajetController et ouvrir la vue
        TrajetController.setTrajetToEdit(selectedTrajet);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("TrajetView.fxml"));
            Parent trajetView = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(trajetView));
            stage.setTitle("Modifier Trajet");
            stage.showAndWait();  // Ouvrir en modal pour attendre la fermeture
            refreshList();  // Actualiser après modification
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la vue de modification.");
        }
    }

    @FXML
    private void supprimerTrajet() {
        Trajet selectedTrajet = trajetsTable.getSelectionModel().getSelectedItem();
        if (selectedTrajet == null) {
            showAlert("Erreur", "Veuillez sélectionner un trajet à supprimer.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Supprimer le trajet ?");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer ce trajet ?");
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                trajetDAO.deleteTrajet(selectedTrajet.getId());
                refreshList();
                showAlert("Succès", "Trajet supprimé avec succès.");
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}