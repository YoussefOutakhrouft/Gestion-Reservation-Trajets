package com.example.javafx_project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

public class CreateUserController {
    @FXML
    private TableView<User> usersTable;
    @FXML
    private TableColumn<User, Integer> idColumn;
    @FXML
    private TableColumn<User, String> nomColumn;
    @FXML
    private TableColumn<User, String> prenomColumn;
    @FXML
    private TableColumn<User, String> emailColumn;
    @FXML
    private TableColumn<User, String> roleColumn;
    @FXML
    private TableColumn<User, Void> actionColumn;

    @FXML
    private TextField nomField;
    @FXML
    private TextField prenomField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ComboBox<String> roleComboBox;
    @FXML
    private Button createUserBtn;
    @FXML
    private Label messageLabel;
    @FXML
    private Label userCountLabel;

    private UserDAO userDAO = new UserDAO();
    private User editingUser = null;
    @FXML
    private void initialize() {
        // Initialise les colonnes
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        // Initialise la colonne Actions
        actionColumn.setCellFactory(createActionCellFactory());
        // Initialise le ComboBox avec les rôles
        roleComboBox.setItems(FXCollections.observableArrayList("ADMIN", "GEST"));
        roleComboBox.setValue("GEST"); // Valeur par défaut
        // Charge la liste des utilisateurs
        loadUsers();
        // Cache le message au départ
        messageLabel.setVisible(false);
        messageLabel.setManaged(false);
    }

    public Callback<TableColumn<User, Void>, TableCell<User, Void>> createActionCellFactory() {
        return param -> new TableCell<>() {
            private final Button editBtn = new Button("✏️ Modifier");
            private final Button deleteBtn = new Button("🗑️ Supprimer");
            private final HBox pane = new HBox(10, editBtn, deleteBtn);
            {
                pane.setAlignment(javafx.geometry.Pos.CENTER);
                editBtn.getStyleClass().add("action-button");
                deleteBtn.getStyleClass().addAll("action-button", "delete-button");

                editBtn.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    editUser(user);
                });

                deleteBtn.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    deleteUser(user);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        };
    }

    private void loadUsers() {
        ObservableList<User> users = FXCollections.observableArrayList(userDAO.getAllUsers());
        usersTable.setItems(users);
    }

    private void updateUserCount(int count) {
        if (userCountLabel != null) {
            userCountLabel.setText(count + (count > 1 ? " utilisateurs" : " utilisateur"));
        }
    }

    private void editUser(User user) {
        editingUser = user;
        nomField.setText(user.getNom());
        prenomField.setText(user.getPrenom());
        emailField.setText(user.getEmail());
        passwordField.setText(""); // Ne pré-remplit pas pour sécurité
        roleComboBox.setValue(user.getRole());
        createUserBtn.setText("Modifier Utilisateur");
        hideMessage();

        // Scroll vers le formulaire
        nomField.requestFocus();
    }
    private void deleteUser(User user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Êtes-vous sûr de vouloir supprimer l'utilisateur ?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer cet utilisateur");

        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            if (userDAO.deleteUser(user.getId())) {
                showMessage("✅ Utilisateur supprimé avec succès !", "success");
                loadUsers();
            } else {
                showMessage("❌ Erreur lors de la suppression de l'utilisateur.", "error");
            }
        }
    }

    @FXML
    private void createUser() {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String role = roleComboBox.getValue();
        // Validation basique
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || role == null) {
            showMessage("⚠️ Veuillez remplir tous les champs obligatoires.", "error");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showMessage("⚠️ Format d'email invalide.", "error");
            return;
        }

        if (editingUser != null) {
            // Modification
            editingUser.setNom(nom);
            editingUser.setPrenom(prenom);
            editingUser.setEmail(email);
            editingUser.setRole(role);
            if (!password.isEmpty()) {
                editingUser.setMotDePass(password);
            }
            if (userDAO.updateUser(editingUser)) {
                showMessage("✅ Utilisateur modifié avec succès !", "success");
                clearForm();
                loadUsers();
            } else {
                showMessage("❌ Erreur lors de la modification de l'utilisateur.", "error");
            }
        } else {
            // Création
            if (password.isEmpty()) {
                showMessage("⚠️ Le mot de passe est requis pour créer un utilisateur.", "error");
                return;
            }
            if (password.length() < 6) {
                showMessage("⚠️ Le mot de passe doit contenir au moins 6 caractères.", "error");
                return;
            }
            User newUser = new User(nom, prenom, email, password, role);
            if (userDAO.insertUser(newUser)) {
                showMessage("✅ Utilisateur créé avec succès !", "success");
                clearForm();
                loadUsers();
            } else {
                showMessage("❌ Erreur lors de la création de l'utilisateur.", "error");
            }
        }
    }

    @FXML
    public void clearForm() {
        editingUser = null;
        nomField.clear();
        prenomField.clear();
        emailField.clear();
        passwordField.clear();
        roleComboBox.setValue("GEST");
        createUserBtn.setText("✅ Créer Utilisateur");
        hideMessage();
    }

    private void showMessage(String message, String type) {
        messageLabel.setText(message);
        messageLabel.setVisible(true);
        messageLabel.setManaged(true);

        if (type.equals("success")) {
            messageLabel.setStyle("-fx-text-fill: #16a34a; -fx-background-color: #f0fdf4; -fx-border-color: #bbf7d0;");
        } else {
            messageLabel.setStyle("-fx-text-fill: #dc2626; -fx-background-color: #fef2f2; -fx-border-color: #fecaca;");
        }
    }

    private void hideMessage() {
        messageLabel.setText("");
        messageLabel.setVisible(false);
        messageLabel.setManaged(false);
    }
}
