package com.example.javafx_project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/transportproject";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    // Méthode pour charger explicitement le driver JDBC
    private static void loadDriver() throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
    }

    // Méthode pour établir une connexion à la base de données
    public static Connection getConnection() {
        Connection connection = null;
        try {
            // Charger le driver JDBC pour MySQL
            loadDriver();

            // Etablir la connexion
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Vérifier la validité de la connexion
            if (connection != null && connection.isValid(2)) {
                System.out.println("Connexion réussie à la base de données MySQL !");
            } else {
                System.out.println("Connexion échouée à la base de données MySQL !");
            }
        } catch (SQLException e) {
            System.out.println("Erreur de connexion à la base de données : " + e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Pilote JDBC introuvable !");
            e.printStackTrace();
        }
        return connection;
    }

    // Méthode pour fermer la connexion
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connexion à la base de données fermée.");
            } catch (SQLException e) {
                System.out.println("Erreur lors de la fermeture de la connexion.");
                e.printStackTrace();
            }
        }
    }

    // Méthode pour créer les tables si elles n'existent pas
    public void connect() {
        String createTableQueryUtilisateur = """
            CREATE TABLE IF NOT EXISTS Utilisateur (
                id INT AUTO_INCREMENT PRIMARY KEY,
                nom VARCHAR(100) NOT NULL,
                prenom VARCHAR(100) NOT NULL,
                email VARCHAR(100) NOT NULL UNIQUE,
                motDePass VARCHAR(255) NOT NULL,
                role ENUM('ADMIN', 'GEST') NOT NULL
            );""";

        String createTableQueryVehicule = """
            CREATE TABLE IF NOT EXISTS Vehicule (
                id INT AUTO_INCREMENT PRIMARY KEY,
                numero_matricule VARCHAR(50) NOT NULL UNIQUE,
                marque VARCHAR(100) NOT NULL,
                nombre_places INT NOT NULL
            );""";

        String createTableQueryTrajet = """
            CREATE TABLE IF NOT EXISTS Trajet (
                id INT AUTO_INCREMENT PRIMARY KEY,
                heure_depart VARCHAR(10) NOT NULL,
                ville_depart VARCHAR(100) NOT NULL,
                ville_arrivee VARCHAR(100) NOT NULL,
                date_trajet DATE NOT NULL,
                heure_arrivee VARCHAR(10) NOT NULL,
                places_restantes INT NOT NULL,
                vehicule_id INT NOT NULL,
                FOREIGN KEY (vehicule_id) REFERENCES Vehicule(id) ON DELETE CASCADE
            );""";

        String createTableQueryArret = """
            CREATE TABLE IF NOT EXISTS Arret (
                id INT AUTO_INCREMENT PRIMARY KEY,
                trajet_id INT NOT NULL,
                ville VARCHAR(100) NOT NULL,
                heure_arrivee VARCHAR(10) NOT NULL,
                temps_reste INT NOT NULL,  -- En minutes
                FOREIGN KEY (trajet_id) REFERENCES Trajet(id) ON DELETE CASCADE
            );""";

        String createTableQueryPrix = """
            CREATE TABLE IF NOT EXISTS Prix (
                id INT AUTO_INCREMENT PRIMARY KEY,
                trajet_id INT NOT NULL,
                ville_depart VARCHAR(100) NOT NULL,
                ville_arrivee VARCHAR(100) NOT NULL,
                prix DECIMAL(10,2) NOT NULL,  -- Prix en décimal (ex. 50.00)
                FOREIGN KEY (trajet_id) REFERENCES Trajet(id) ON DELETE CASCADE,
                UNIQUE(trajet_id, ville_depart, ville_arrivee)  -- Empêche les doublons pour une paire dans un trajet
            );""";



        try (Connection connection = getConnection()) {
            if (connection != null) {
                executeQuery(connection, createTableQueryUtilisateur);
                executeQuery(connection, createTableQueryVehicule);
                executeQuery(connection, createTableQueryTrajet);
                executeQuery(connection, createTableQueryArret);
                executeQuery(connection, createTableQueryPrix);
                System.out.println("Base de données et tables prêtes !");

                // Ajoute l'utilisateur ADMIN par défaut après la création des tables
                new UserDAO().insertDefaultAdminUser();
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la connexion ou création des tables : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Méthode utilitaire pour exécuter une requête
    private void executeQuery(Connection connection, String query) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.execute();
        }
    }
}
