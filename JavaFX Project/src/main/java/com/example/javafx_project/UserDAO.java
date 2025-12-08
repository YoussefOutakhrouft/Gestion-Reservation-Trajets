package com.example.javafx_project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    // Méthode pour insérer un nouvel utilisateur
    public boolean insertUser(User user) {
        String query = "INSERT INTO Utilisateur (nom, prenom, email, motDePass, role) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, user.getNom());
            stmt.setString(2, user.getPrenom());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, hashPassword(user.getMotDePass())); // Hash le mot de passe
            stmt.setString(5, user.getRole());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'insertion de l'utilisateur : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Méthode pour vérifier le login (retourne l'utilisateur si valide, null sinon)
    public User login(String email, String motDePass) {
        String query = "SELECT * FROM Utilisateur WHERE email = ? AND motDePass = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setString(2, hashPassword(motDePass)); // Vérifie le hash
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setNom(rs.getString("nom"));
                user.setPrenom(rs.getString("prenom"));
                user.setEmail(rs.getString("email"));
                user.setMotDePass(rs.getString("motDePass"));
                user.setRole(rs.getString("role"));
                return user;
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors du login : " + e.getMessage());
            e.printStackTrace();
        }
        return null; // Login échoué
    }

    // Méthode pour insérer un utilisateur ADMIN par défaut (si aucun n'existe)
    public void insertDefaultAdminUser() {
        // Vérifie si un utilisateur ADMIN existe déjà
        String checkQuery = "SELECT COUNT(*) FROM Utilisateur WHERE role = 'ADMIN'";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
             ResultSet rs = checkStmt.executeQuery()) {
            if (rs.next() && rs.getInt(1) == 0) { // Aucun ADMIN trouvé
                // Insère l'utilisateur par défaut
                User defaultAdmin = new User("Admin", "Default", "test@gmail.com", "test1234", "ADMIN");
                if (insertUser(defaultAdmin)) {
                    System.out.println("Utilisateur ADMIN par défaut ajouté avec succès.");
                } else {
                    System.out.println("Erreur lors de l'ajout de l'utilisateur ADMIN par défaut.");
                }
            } else {
                System.out.println("Un utilisateur ADMIN existe déjà. Aucun ajout par défaut.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la vérification/ajout de l'utilisateur ADMIN par défaut : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Méthode simple pour hasher le mot de passe (utilise BCrypt en production !)
    private String hashPassword(String password) {
        // Exemple basique : en production, utilise BCrypt ou Argon2
        return Integer.toString(password.hashCode()); // Remplace par un vrai hash
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM Utilisateur";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setNom(rs.getString("nom"));
                user.setPrenom(rs.getString("prenom"));
                user.setEmail(rs.getString("email"));
                user.setMotDePass(rs.getString("motDePass"));
                user.setRole(rs.getString("role"));
                users.add(user);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des utilisateurs : " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }

    // Méthode pour mettre à jour un utilisateur
    public boolean updateUser(User user) {
        String query = "UPDATE Utilisateur SET nom = ?, prenom = ?, email = ?, motDePass = ?, role = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, user.getNom());
            stmt.setString(2, user.getPrenom());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getMotDePass() != null ? hashPassword(user.getMotDePass()) : user.getMotDePass());
            stmt.setString(5, user.getRole());
            stmt.setInt(6, user.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erreur update : " + e.getMessage());
            return false;
        }
    }

    // Méthode pour supprimer un utilisateur
    public boolean deleteUser(int id) {
        String query = "DELETE FROM Utilisateur WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erreur delete : " + e.getMessage());
            return false;
        }
    }

}
