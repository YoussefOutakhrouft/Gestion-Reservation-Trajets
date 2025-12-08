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
    }

    private Callback<TableColumn<User, Void>, TableCell<User, Void>> createActionCellFactory() {
        return param -> new TableCell<>() {
            private final Button editBtn = new Button("Modifier");
            private final Button deleteBtn = new Button("Supprimer");
            private final HBox pane = new HBox(editBtn, deleteBtn);
            {
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

    private void editUser(User user) {
        editingUser = user;
        nomField.setText(user.getNom());
        prenomField.setText(user.getPrenom());
        emailField.setText(user.getEmail());
        passwordField.setText(""); // Ne pré-remplit pas pour sécurité
        roleComboBox.setValue(user.getRole());
        createUserBtn.setText("Modifier Utilisateur");
    }
    private void deleteUser(User user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer cet utilisateur ?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            if (userDAO.deleteUser(user.getId())) {
                messageLabel.setStyle("-fx-text-fill: green;");
                messageLabel.setText("Utilisateur supprimé !");
                loadUsers();
            } else {
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText("Erreur lors de la suppression.");
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
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Veuillez remplir tous les champs.");
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
                messageLabel.setStyle("-fx-text-fill: green;");
                messageLabel.setText("Utilisateur modifié !");
                clearForm();
                loadUsers();
            } else {
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText("Erreur lors de la modification.");
            }
        } else {
            // Création
            if (password.isEmpty()) {
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText("Mot de passe requis pour la création.");
                return;
            }
            User newUser = new User(nom, prenom, email, password, role);
            if (userDAO.insertUser(newUser)) {
                messageLabel.setStyle("-fx-text-fill: green;");
                messageLabel.setText("Utilisateur créé !");
                clearForm();
                loadUsers();
            } else {
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText("Erreur lors de la création.");
            }
        }
    }

    private void clearForm() {
        editingUser = null;
        nomField.clear();
        prenomField.clear();
        emailField.clear();
        passwordField.clear();
        roleComboBox.setValue("GEST");
        createUserBtn.setText("Créer Utilisateur");
    }
}
