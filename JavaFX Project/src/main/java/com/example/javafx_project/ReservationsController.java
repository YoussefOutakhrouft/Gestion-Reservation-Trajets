package com.example.javafx_project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

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

    private ReservationDAO reservationDAO = new ReservationDAO();
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
    }
}
