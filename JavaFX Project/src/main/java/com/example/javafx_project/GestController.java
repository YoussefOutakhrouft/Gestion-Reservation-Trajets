package com.example.javafx_project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class GestController {
    @FXML
    private Button logoutBtn, mesTachesBtn, reservationsBtn, searchBtn, reserverBtn;
    @FXML
    private Label contentLabel;
    @FXML
    private TextField villeDepartField, villeArriveeField, nomField, prenomField, cinField, placeField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TableView<Trajet> trajetsTable;
    @FXML
    private TableColumn<Trajet, Integer> trajetIdCol, placesCol;
    @FXML
    private TableColumn<Trajet, String> villeDepartCol, villeArriveeCol, dateCol;
    @FXML
    private ComboBox<ReservationDAO.Segment> segmentComboBox;

    private ReservationDAO reservationDAO = new ReservationDAO();
    private ObservableList<Trajet> trajetsList = FXCollections.observableArrayList();
    private ObservableList<ReservationDAO.Segment> segmentsList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configurer la TableView pour les trajets
        trajetIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        villeDepartCol.setCellValueFactory(new PropertyValueFactory<>("villeDepart"));
        villeArriveeCol.setCellValueFactory(new PropertyValueFactory<>("villeArrivee"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("dateTrajet"));
        placesCol.setCellValueFactory(new PropertyValueFactory<>("placesRestantes"));
        trajetsTable.setItems(trajetsList);
        segmentComboBox.setItems(segmentsList);

        trajetsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                segmentsList.setAll(reservationDAO.getSegmentsForTrajet(newSelection.getId()));
            } else {
                segmentsList.clear();
            }
        });
    }

    @FXML
    private void showMesTaches() {
        contentLabel.setText("Mes Tâches - Ici, affiche les tâches assignées à l'utilisateur.");
        // Tu peux étendre pour charger un FXML séparé ou une TableView ici
    }

    @FXML
    private void showReservations() {
        try {
            // Charger une nouvelle vue pour afficher les réservations (TableView)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ReservationsView.fxml"));  // Créez ce FXML séparément
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des Réservations");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void searchTrajets() {
        String depart = villeDepartField.getText();
        String arrivee = villeArriveeField.getText();
        String date = datePicker.getValue() != null ? datePicker.getValue().toString() : "";
        trajetsList.setAll(reservationDAO.searchTrajets(depart, arrivee, date));
    }

    @FXML
    private void reserverBillet() {
        Trajet selectedTrajet = trajetsTable.getSelectionModel().getSelectedItem();
        ReservationDAO.Segment selectedSegment = segmentComboBox.getSelectionModel().getSelectedItem();
        if (selectedTrajet == null || selectedSegment == null) {
            showAlert("Erreur", "Sélectionnez un trajet.");
            return;
        }
        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String cin = cinField.getText();
        int place;
        try {
            place = Integer.parseInt(placeField.getText());
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Numéro de place invalide.");
            return;
        }

        if (reservationDAO.reserverBillet(selectedTrajet.getId(), selectedSegment.getVilleDepart(), selectedSegment.getVilleArrivee(), nom, prenom, cin, place)) {
            showAlert("Succès", "Réservation effectuée ! Billet généré.");
            genererBillet(nom, prenom, cin, selectedTrajet, selectedSegment, place);
            searchTrajets();  // Rafraîchir la liste
        } else {
            showAlert("Erreur", "Réservation échouée (place indisponible ou erreur).");
        }
    }

    // Dans GestController.java, remplacez la méthode genererBillet existante par :
    private void genererBillet(String nom, String prenom, String cin, Trajet trajet, ReservationDAO.Segment segment, int place) {
        // Créer un objet Reservation temporaire pour passer les données (ou récupérez-le depuis la DB si nécessaire)
        Reservation reservation = new Reservation(0, trajet.getId(), segment.getVilleDepart(), segment.getVilleArrivee(), nom, prenom, cin, place);

        // Générer le PDF
        PdfGenerator.genererBilletPdf(reservation, trajet, segment);

        // Optionnel : Afficher un message de confirmation
        showAlert("Succès", "Billet PDF généré et sauvegardé !");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
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
