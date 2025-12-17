package com.example.javafx_project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;

public class ReservationsController {
    @FXML
    private TableView<Reservation> reservationsTable;
    @FXML
    private TableColumn<Reservation, Integer> idCol;
    @FXML
    private TableColumn<Reservation, Integer> trajetIdCol;
    @FXML
    private TableColumn<Reservation, String> nomCol;
    @FXML
    private TableColumn<Reservation, String> prenomCol;
    @FXML
    private TableColumn<Reservation, String> cinCol;
    @FXML
    private TableColumn<Reservation, Integer> placeCol;
    @FXML
    private TableColumn<Reservation, String> villeDepartCol;
    @FXML
    private TableColumn<Reservation, String> villeArriveeCol;
    @FXML
    private TableColumn<Reservation, Void> telechargerCol;
    @FXML
    private Button backBtn;

    private ReservationDAO reservationDAO = new ReservationDAO();
    private TrajetDAO trajetDAO = new TrajetDAO();
    private ObservableList<Reservation> reservationsList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configurer les colonnes de la TableView
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        trajetIdCol.setCellValueFactory(new PropertyValueFactory<>("trajetId"));
        villeDepartCol.setCellValueFactory(new PropertyValueFactory<>("villeDepart"));  // Ajouté
        villeArriveeCol.setCellValueFactory(new PropertyValueFactory<>("villeArrivee"));  // Ajouté
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomCol.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        cinCol.setCellValueFactory(new PropertyValueFactory<>("cin"));
        placeCol.setCellValueFactory(new PropertyValueFactory<>("place"));
        // Charger les données des réservations
        reservationsList.setAll(reservationDAO.getAllReservations());
        reservationsTable.setItems(reservationsList);

        telechargerCol.setCellFactory(new Callback<TableColumn<Reservation, Void>, TableCell<Reservation, Void>>() {
            @Override
            public TableCell<Reservation, Void> call(final TableColumn<Reservation, Void> param) {
                final TableCell<Reservation, Void> cell = new TableCell<Reservation, Void>() {
                    private final Button btn = new Button("📥 Télécharger");
                    {
                        btn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
                        btn.setOnAction(event -> {
                            Reservation reservation = getTableView().getItems().get(getIndex());
                            telechargerBillet(reservation);
                        });
                    }
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        });
        // Charger les données des réservations
        reservationsList.setAll(reservationDAO.getAllReservations());
        reservationsTable.setItems(reservationsList);
    }

    private void telechargerBillet(Reservation reservation) {
        Trajet trajet = trajetDAO.getTrajetById(reservation.getTrajetId());
        if (trajet == null) {
            showAlert("Erreur", "Trajet introuvable pour cette réservation.");
            return;
        }
        // Récupérer le segment depuis ReservationDAO
        ReservationDAO.Segment segment = reservationDAO.getSegmentForReservation(reservation.getTrajetId(), reservation.getVilleDepart(), reservation.getVilleArrivee());
        if (segment == null) {
            showAlert("Erreur", "Segment introuvable pour cette réservation.");
            return;
        }
        // Générer le PDF (comme dans GestController)
        PdfGenerator.genererBilletPdf(reservation, trajet, segment);
        // Afficher une alerte de succès
        showAlert("Succès", "Billet PDF généré et sauvegardé !");
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void goBack() {
        Stage stage = (Stage) backBtn.getScene().getWindow();
        stage.close();
    }
}
