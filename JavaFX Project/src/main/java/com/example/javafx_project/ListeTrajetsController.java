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
    @FXML
    private Label trajetCountLabel;
    @FXML
    private Label totalTrajetsLabel;
    @FXML
    private Label totalPlacesLabel;
    @FXML
    private Label prochainTrajetLabel;

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
        placesRestantesColumn.setCellFactory(column -> new TableCell<Trajet, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.valueOf(item));
                    // Coloration selon le nombre de places
                    if (item == 0) {
                        setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;");
                    } else if (item <= 2) {
                        setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
                    }
                }
            }
        });

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
        updateStatistics();
    }

    private void updateStatistics() {
        int trajetCount = trajets.size();

        // Mise à jour du compteur de trajets
        if (trajetCountLabel != null) {
            trajetCountLabel.setText(trajetCount + (trajetCount > 1 ? " trajets" : " trajet"));
        }

        // Mise à jour des statistiques détaillées
        if (totalTrajetsLabel != null) {
            totalTrajetsLabel.setText(String.valueOf(trajetCount));
        }

        // Calcul du nombre total de places disponibles
        if (totalPlacesLabel != null) {
            int totalPlaces = trajets.stream()
                    .mapToInt(Trajet::getPlacesRestantes)
                    .sum();
            totalPlacesLabel.setText(String.valueOf(totalPlaces));
        }

        // Affichage du prochain trajet
        if (prochainTrajetLabel != null) {
            String prochainTrajet = getProchainTrajet();
            prochainTrajetLabel.setText(prochainTrajet);
        }
    }

    private String getProchainTrajet() {
        if (trajets.isEmpty()) {
            return "--:--";
        }

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");

        Trajet prochain = null;
        LocalDateTime prochainDateTime = null;

        for (Trajet trajet : trajets) {
            try {
                LocalTime heureDepart = LocalTime.parse(trajet.getHeureDepart(), formatter);
                LocalDateTime trajetDateTime = LocalDateTime.of(trajet.getDateTrajet(), heureDepart);

                if (trajetDateTime.isAfter(now)) {
                    if (prochainDateTime == null || trajetDateTime.isBefore(prochainDateTime)) {
                        prochainDateTime = trajetDateTime;
                        prochain = trajet;
                    }
                }
            } catch (Exception e) {
                // Ignorer les erreurs de parsing
            }
        }

        if (prochain != null) {
            DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("dd/MM HH:mm");
            return prochainDateTime.format(displayFormatter);
        }

        return "--:--";
    }

    @FXML
    private void modifierTrajet() {
        Trajet selectedTrajet = trajetsTable.getSelectionModel().getSelectedItem();
        if (selectedTrajet == null) {
            showAlert(
                    "Aucune sélection",
                    "Veuillez sélectionner un trajet à modifier.",
                    Alert.AlertType.WARNING
            );
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
            showAlert(
                    "Erreur",
                    "Impossible d'ouvrir la vue de modification.",
                    Alert.AlertType.ERROR
            );
        }
    }

    @FXML
    private void supprimerTrajet() {
        Trajet selectedTrajet = trajetsTable.getSelectionModel().getSelectedItem();
        if (selectedTrajet == null) {
            showAlert(
                    "Aucune sélection",
                    "Veuillez sélectionner un trajet à supprimer.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Supprimer le trajet ?");
        confirmation.setContentText(
                "Êtes-vous sûr de vouloir supprimer le trajet de " +
                        selectedTrajet.getVilleDepart() + " à " +
                        selectedTrajet.getVilleArrivee() + " ?\n\n" +
                        "Cette action est irréversible."
        );
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    trajetDAO.deleteTrajet(selectedTrajet.getId());
                    showAlert(
                            "Succès",
                            "Trajet supprimé avec succès.",
                            Alert.AlertType.INFORMATION
                    );
                    refreshList();
                } catch (Exception e) {
                    showAlert(
                            "Erreur",
                            "Une erreur est survenue lors de la suppression.",
                            Alert.AlertType.ERROR
                    );
                    e.printStackTrace();
                }
            }
        });
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}