package com.example.javafx_project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class TrajetController {
    @FXML
    private DatePicker dateTrajetPicker;
    @FXML
    private TextField heureDepartField;
    @FXML
    private TextField villeDepartField;
    @FXML
    private TextField villeArriveeField;
    @FXML
    private TextField heureArriveeField;
    @FXML
    private ComboBox<Vehicule> vehiculeComboBox;
    @FXML
    private VBox arretsContainer;
    @FXML
    private VBox prixContainer;
    @FXML
    private Button addArretBtn;
    @FXML
    private Button generatePrixBtn;
    @FXML
    private Button saveBtn;
    @FXML
    private Button cancelBtn;
    private ObservableList<Vehicule> vehicules = FXCollections.observableArrayList();
    private List<Arret> arrets = new ArrayList<>();
    private List<Prix> prix = new ArrayList<>();
    private static Trajet trajetToEdit = null;
    private boolean isEditing = false;

    @FXML
    public void initialize() {
        // Charger les véhicules depuis la base de données
        VehiculeDAO vehiculeDAO = new VehiculeDAO();
        vehicules.addAll(vehiculeDAO.getAllVehicules());
        vehiculeComboBox.setItems(vehicules);
        if (trajetToEdit != null) {
            isEditing = true;
            prefillFields(trajetToEdit);
        }
    }

    public static void setTrajetToEdit(Trajet trajet) {
        trajetToEdit = trajet;
    }

    private void prefillFields(Trajet trajet) {
        dateTrajetPicker.setValue(trajet.getDateTrajet());
        heureDepartField.setText(trajet.getHeureDepart());
        villeDepartField.setText(trajet.getVilleDepart());
        villeArriveeField.setText(trajet.getVilleArrivee());
        heureArriveeField.setText(trajet.getHeureArrivee());
        vehiculeComboBox.setValue(trajet.getVehicule());
        // Pré-remplir les arrêts (simplifié ; vous pouvez charger depuis la base si nécessaire)
        // Pour cet exemple, on suppose que les arrêts sont vides ou à charger séparément
        // Ici, on ne pré-remplit pas les arrêts/prix pour simplicité ; étendez si besoin
    }

    @FXML
    private void addArret() {
        // Créer des champs pour un nouvel arrêt
        HBox arretBox = new HBox(10);
        TextField villeField = new TextField();
        villeField.setPromptText("Ville");
        TextField heureArriveeField = new TextField();
        heureArriveeField.setPromptText("Heure arrivée (Ex: 10:00)");
        TextField tempsResteField = new TextField();
        tempsResteField.setPromptText("Temps de reste (min)");
        Button removeBtn = new Button("Supprimer");
        removeBtn.setOnAction(e -> arretsContainer.getChildren().remove(arretBox));
        arretBox.getChildren().addAll(villeField, heureArriveeField, tempsResteField, removeBtn);
        arretsContainer.getChildren().add(arretBox);
    }

    @FXML
    private void generatePrix() {
        // Collecter toutes les villes : départ + arrêts + arrivée
        List<String> villes = new ArrayList<>();
        villes.add(villeDepartField.getText());
        for (var node : arretsContainer.getChildren()) {
            if (node instanceof HBox) {
                HBox box = (HBox) node;
                TextField villeField = (TextField) box.getChildren().get(0);
                if (!villeField.getText().isEmpty()) {
                    villes.add(villeField.getText());
                }
            }
        }
        villes.add(villeArriveeField.getText());
        // Générer les paires uniques et créer des champs de prix
        prixContainer.getChildren().clear();
        prix.clear();
        for (int i = 0; i < villes.size(); i++) {
            for (int j = i + 1; j < villes.size(); j++) {
                String ville1 = villes.get(i);
                String ville2 = villes.get(j);
                HBox prixBox = new HBox(10);
                Label label = new Label("Prix " + ville1 + " -> " + ville2 + " :");
                TextField prixField = new TextField();
                prixField.setPromptText("Ex: 50.00");
                prixBox.getChildren().addAll(label, prixField);
                prixContainer.getChildren().add(prixBox);
            }
        }
    }

    @FXML
    private void saveTrajet() {
        // Valider les champs principaux
        if (dateTrajetPicker.getValue() == null || heureDepartField.getText().isEmpty() || villeDepartField.getText().isEmpty() ||
                villeArriveeField.getText().isEmpty() || heureArriveeField.getText().isEmpty() || vehiculeComboBox.getValue() == null) {
            showAlert("Erreur", "Veuillez remplir tous les champs principaux.");
            return;
        }
        // Vérifier la disponibilité du véhicule
        TrajetDAO trajetDAO = new TrajetDAO();
        Integer excludeId = isEditing ? trajetToEdit.getId() : null;
        boolean available = trajetDAO.isVehiculeAvailable(
                vehiculeComboBox.getValue().getId(),
                dateTrajetPicker.getValue(),
                heureDepartField.getText(),
                heureArriveeField.getText(),
                excludeId
        );
        if (!available) {
            showAlert("Erreur", "Le véhicule n'est pas disponible à ces horaires. Veuillez choisir un autre véhicule ou modifier les horaires.");
            return;
        }
        // Collecter les arrêts
        arrets.clear();
        for (var node : arretsContainer.getChildren()) {
            if (node instanceof HBox) {
                HBox box = (HBox) node;
                TextField villeField = (TextField) box.getChildren().get(0);
                TextField heureArriveeField = (TextField) box.getChildren().get(1);
                TextField tempsResteField = (TextField) box.getChildren().get(2);
                if (!villeField.getText().isEmpty() && !heureArriveeField.getText().isEmpty() && !tempsResteField.getText().isEmpty()) {
                    try {
                        int tempsReste = Integer.parseInt(tempsResteField.getText());
                        arrets.add(new Arret(villeField.getText(), heureArriveeField.getText(), tempsReste));
                    } catch (NumberFormatException e) {
                        showAlert("Erreur", "Le temps de reste doit être un nombre entier.");
                        return;
                    }
                }
            }
        }
        prix.clear();
        List<String> villes = new ArrayList<>();
        villes.add(villeDepartField.getText());
        for (Arret arret : arrets) {
            villes.add(arret.getVille());
        }
        villes.add(villeArriveeField.getText());
        int index = 0;

        for (int i = 0; i < villes.size(); i++) {
            for (int j = i + 1; j < villes.size(); j++) {
                if (index < prixContainer.getChildren().size()) {
                    HBox prixBox = (HBox) prixContainer.getChildren().get(index);
                    TextField prixField = (TextField) prixBox.getChildren().get(1);
                    if (!prixField.getText().isEmpty()) {
                        try {
                            double prixValue = Double.parseDouble(prixField.getText());
                            prix.add(new Prix(villes.get(i), villes.get(j), prixValue));
                        } catch (NumberFormatException e) {
                            showAlert("Erreur", "Le prix doit être un nombre décimal.");
                            return;
                        }
                    }
                    index++;
                }
            }
        }
        // Calculer les places restantes (initialement égal au nombre de places du véhicule)
        int placesRestantes = vehiculeComboBox.getValue().getNombrePlaces();

        // Créer et sauvegarder le trajet
        Trajet trajet = new Trajet(
                dateTrajetPicker.getValue(),
                heureDepartField.getText(),
                villeDepartField.getText(),
                villeArriveeField.getText(),
                heureArriveeField.getText(),
                vehiculeComboBox.getValue(),
                placesRestantes,
                arrets,
                prix
        );
        if (isEditing && trajetToEdit != null) {
            trajet.setId(trajetToEdit.getId());  // Conserver l'ID pour la mise à jour
            trajetDAO.updateTrajet(trajet);
            showAlert("Succès", "Trajet modifié avec succès !");
        } else {
            trajetDAO.insertTrajet(trajet);
            showAlert("Succès", "Trajet créé avec succès !");
        }
        clearForm();
        trajetToEdit = null;  // Réinitialiser
        isEditing = false;
    }
    @FXML
    private void cancel() {
        // Retourner à la vue admin (ou fermer la vue actuelle)
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
        dateTrajetPicker.setValue(null);
        heureDepartField.clear();
        villeDepartField.clear();
        villeArriveeField.clear();
        heureArriveeField.clear();
        vehiculeComboBox.setValue(null);
        arretsContainer.getChildren().clear();
        arrets.clear();
    }
}
