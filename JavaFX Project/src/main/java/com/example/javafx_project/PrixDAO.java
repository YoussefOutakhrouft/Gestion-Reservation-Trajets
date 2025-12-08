package com.example.javafx_project;

import com.example.javafx_project.DatabaseConnection;
import com.example.javafx_project.Prix;

import java.sql.*;
import java.util.List;

public class PrixDAO {
    public void insertPrix(int trajetId, List<Prix> prixList) {
        String query = "INSERT INTO Prix (trajet_id, ville_depart, ville_arrivee, prix) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            for (Prix prix : prixList) {
                stmt.setInt(1, trajetId);
                stmt.setString(2, prix.getVilleDepart());
                stmt.setString(3, prix.getVilleArrivee());
                stmt.setDouble(4, prix.getPrix());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
