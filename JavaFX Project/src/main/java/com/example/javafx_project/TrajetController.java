package com.example.javafx_project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class TrajetController {
    @FXML
    private Label headerTitle;
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
        clearEmptyStates();
        if (trajetToEdit != null) {
            isEditing = true;
            prefillFields(trajetToEdit);
            if (headerTitle != null) {
                headerTitle.setText("✏️ Modifier le Trajet");
            }
        }
    }

    public static void setTrajetToEdit(Trajet trajet) {
        trajetToEdit = trajet;
    }

    private void clearEmptyStates() {
        // Supprimer les labels d'état vide s'ils existent
        arretsContainer.getChildren().removeIf(node ->
                node instanceof Label && ((Label) node).getStyleClass().contains("empty-state")
        );
        prixContainer.getChildren().removeIf(node ->
                node instanceof Label && ((Label) node).getStyleClass().contains("empty-state")
        );
    }

    private void prefillFields(Trajet trajet) {
        dateTrajetPicker.setValue(trajet.getDateTrajet());
        heureDepartField.setText(trajet.getHeureDepart());
        villeDepartField.setText(trajet.getVilleDepart());
        villeArriveeField.setText(trajet.getVilleArrivee());
        heureArriveeField.setText(trajet.getHeureArrivee());
        vehiculeComboBox.setValue(trajet.getVehicule());
        if (trajet.getArrets() != null && !trajet.getArrets().isEmpty()) {
            clearEmptyStates();
            for (Arret arret : trajet.getArrets()) {
                addArretRow(arret.getVille(), arret.getHeureArrivee(),
                        String.valueOf(arret.getTempsReste()));
            }
        }
    }

    @FXML
    private void addArret() {
        clearEmptyStates();
        addArretRow("", "", "");
    }

    private void addArretRow(String ville, String heure, String temps) {
        // Créer une ligne stylisée pour un arrêt
        HBox arretBox = new HBox(12);
        arretBox.setAlignment(Pos.CENTER_LEFT);
        arretBox.getStyleClass().add("arret-row");

        // Champ ville
        TextField villeField = new TextField(ville);
        villeField.setPromptText("Nom de la ville");
        villeField.setPrefWidth(180);
        HBox.setHgrow(villeField, Priority.ALWAYS);

        // Champ heure d'arrivée
        TextField heureArriveeField = new TextField(heure);
        heureArriveeField.setPromptText("HH:MM");
        heureArriveeField.setPrefWidth(100);

        // Champ temps de reste
        TextField tempsResteField = new TextField(temps);
        tempsResteField.setPromptText("Minutes");
        tempsResteField.setPrefWidth(100);

        // Bouton supprimer
        Button removeBtn = new Button("🗑️ Supprimer");
        removeBtn.getStyleClass().add("remove-button");
        removeBtn.setOnAction(e -> {
            arretsContainer.getChildren().remove(arretBox);
            if (arretsContainer.getChildren().isEmpty()) {
                Label emptyLabel = new Label("Aucun arrêt ajouté. Cliquez sur 'Ajouter un Arrêt' pour commencer.");
                emptyLabel.getStyleClass().add("empty-state");
                arretsContainer.getChildren().add(emptyLabel);
            }
        });

        arretBox.getChildren().addAll(villeField, heureArriveeField, tempsResteField, removeBtn);
        arretsContainer.getChildren().add(arretBox);
    }

    @FXML
    private void generatePrix() {
        // Collecter toutes les villes : départ + arrêts + arrivée
        List<String> villes = new ArrayList<>();
        String villeDepart = villeDepartField.getText().trim();
        String villeArrivee = villeArriveeField.getText().trim();
        if (villeDepart.isEmpty() || villeArrivee.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir les villes de départ et d'arrivée avant de générer les prix.", Alert.AlertType.WARNING);
            return;
        }
        villes.add(villeDepart);
        for (var node : arretsContainer.getChildren()) {
            if (node instanceof HBox && node.getStyleClass().contains("arret-row")) {
                HBox box = (HBox) node;
                TextField villeField = (TextField) box.getChildren().get(0);
                String ville = villeField.getText().trim();
                if (!ville.isEmpty()) {
                    villes.add(ville);
                }
            }
        }
        villes.add(villeArriveeField.getText());
        if (villes.size() < 2) {
            showAlert("Erreur", "Il faut au moins une ville de départ et une ville d'arrivée.", Alert.AlertType.WARNING);
            return;
        }
        // Générer les paires uniques et créer des champs de prix
        prixContainer.getChildren().clear();
        prix.clear();
        for (int i = 0; i < villes.size(); i++) {
            for (int j = i + 1; j < villes.size(); j++) {
                String ville1 = villes.get(i);
                String ville2 = villes.get(j);

                HBox prixBox = new HBox(12);
                prixBox.setAlignment(Pos.CENTER_LEFT);
                prixBox.getStyleClass().add("prix-row");

                Label label = new Label("💵 " + ville1 + " → " + ville2 + " :");
                label.setPrefWidth(250);

                TextField prixField = new TextField();
                prixField.setPromptText("Ex: 50.00");
                prixField.setPrefWidth(120);
                HBox.setHgrow(prixField, Priority.ALWAYS);

                Label currencyLabel = new Label("DH");
                currencyLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #6b7280;");

                prixBox.getChildren().addAll(label, prixField, currencyLabel);
                prixContainer.getChildren().add(prixBox);
            }
        }

        showAlert("Succès", "Les champs de prix ont été générés avec succès !", Alert.AlertType.INFORMATION);

    }
    @FXML
    private void saveTrajet() {
        // Valider les champs principaux
        if (dateTrajetPicker.getValue() == null || heureDepartField.getText().trim().isEmpty() ||
                villeDepartField.getText().trim().isEmpty() || villeArriveeField.getText().trim().isEmpty() ||
                heureArriveeField.getText().trim().isEmpty() || vehiculeComboBox.getValue() == null) {
            showAlert("Erreur", "Veuillez remplir tous les champs obligatoires (*)", Alert.AlertType.ERROR);
            return;
        }

        // Vérifier la disponibilité du véhicule
        TrajetDAO trajetDAO = new TrajetDAO();
        Integer excludeId = isEditing ? trajetToEdit.getId() : null;
        boolean available = trajetDAO.isVehiculeAvailable(
                vehiculeComboBox.getValue().getId(),
                dateTrajetPicker.getValue(),
                heureDepartField.getText().trim(),
                heureArriveeField.getText().trim(),
                excludeId
        );

        if (!available) {
            showAlert("Erreur", "Le véhicule n'est pas disponible à ces horaires. Veuillez choisir un autre véhicule ou modifier les horaires.", Alert.AlertType.ERROR);
            return;
        }

        // Collecter les arrêts
        arrets.clear();
        for (var node : arretsContainer.getChildren()) {
            if (node instanceof HBox && node.getStyleClass().contains("arret-row")) {
                HBox box = (HBox) node;
                TextField villeField = (TextField) box.getChildren().get(0);
                TextField heureArriveeField = (TextField) box.getChildren().get(1);
                TextField tempsResteField = (TextField) box.getChildren().get(2);

                String ville = villeField.getText().trim();
                String heure = heureArriveeField.getText().trim();
                String tempsStr = tempsResteField.getText().trim();

                if (!ville.isEmpty() && !heure.isEmpty() && !tempsStr.isEmpty()) {
                    try {
                        int tempsReste = Integer.parseInt(tempsStr);
                        arrets.add(new Arret(ville, heure, tempsReste));
                    } catch (NumberFormatException e) {
                        showAlert("Erreur", "Le temps de reste doit être un nombre entier (minutes).", Alert.AlertType.ERROR);
                        return;
                    }
                }
            }
        }

        // Collecter les prix
        prix.clear();
        List<String> villes = new ArrayList<>();
        villes.add(villeDepartField.getText().trim());
        for (Arret arret : arrets) {
            villes.add(arret.getVille());
        }
        villes.add(villeArriveeField.getText().trim());

        int index = 0;
        for (int i = 0; i < villes.size(); i++) {
            for (int j = i + 1; j < villes.size(); j++) {
                if (index < prixContainer.getChildren().size()) {
                    HBox prixBox = (HBox) prixContainer.getChildren().get(index);
                    TextField prixField = (TextField) prixBox.getChildren().get(1);
                    String prixStr = prixField.getText().trim();

                    if (!prixStr.isEmpty()) {
                        try {
                            double prixValue = Double.parseDouble(prixStr);
                            prix.add(new Prix(villes.get(i), villes.get(j), prixValue));
                        } catch (NumberFormatException e) {
                            showAlert("Erreur", "Le prix doit être un nombre décimal.", Alert.AlertType.ERROR);
                            return;
                        }
                    } else {
                        showAlert("Erreur", "Veuillez remplir tous les champs de prix.", Alert.AlertType.ERROR);
                        return;
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
                heureDepartField.getText().trim(),
                villeDepartField.getText().trim(),
                villeArriveeField.getText().trim(),
                heureArriveeField.getText().trim(),
                vehiculeComboBox.getValue(),
                placesRestantes,
                arrets,
                prix
        );

        if (isEditing && trajetToEdit != null) {
            trajet.setId(trajetToEdit.getId());
            trajetDAO.updateTrajet(trajet);
            showAlert("Succès", "Trajet modifié avec succès !", Alert.AlertType.INFORMATION);
        } else {
            trajetDAO.insertTrajet(trajet);
            showAlert("Succès", "Trajet créé avec succès !", Alert.AlertType.INFORMATION);
        }

        clearForm();
        trajetToEdit = null;
        isEditing = false;

        // Fermer la fenêtre après sauvegarde
        Stage stage = (Stage) saveBtn.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void cancel() {
        // Confirmation avant d'annuler
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Annuler les modifications ?");
        confirmation.setContentText("Êtes-vous sûr de vouloir annuler ? Toutes les modifications seront perdues.");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                clearForm();
                trajetToEdit = null;
                isEditing = false;
                Stage stage = (Stage) cancelBtn.getScene().getWindow();
                stage.close();
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

    private void clearForm() {
        dateTrajetPicker.setValue(null);
        heureDepartField.clear();
        villeDepartField.clear();
        villeArriveeField.clear();
        heureArriveeField.clear();
        vehiculeComboBox.setValue(null);
        arretsContainer.getChildren().clear();
        prixContainer.getChildren().clear();
        arrets.clear();
        prix.clear();

        // Rétablir les états vides
        Label arretEmptyLabel = new Label("Aucun arrêt ajouté. Cliquez sur 'Ajouter un Arrêt' pour commencer.");
        arretEmptyLabel.getStyleClass().add("empty-state");
        arretsContainer.getChildren().add(arretEmptyLabel);

        Label prixEmptyLabel = new Label("Cliquez sur 'Générer les Prix' après avoir ajouté les villes.");
        prixEmptyLabel.getStyleClass().add("empty-state");
        prixContainer.getChildren().add(prixEmptyLabel);
    }
}