package com.example.javafx_project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VehiculeDAO {
    public List<Vehicule> getAllVehicules() {
        List<Vehicule> vehicules = new ArrayList<>();
        String query = "SELECT * FROM Vehicule";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Vehicule v = new Vehicule();
                v.setId(rs.getInt("id"));
                v.setNumeroMatricule(rs.getString("numero_matricule"));
                v.setMarque(rs.getString("marque"));
                v.setNombrePlaces(rs.getInt("nombre_places"));
                vehicules.add(v);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehicules;
    }

    public void insertVehicule(Vehicule vehicule) {
        String query = "INSERT INTO Vehicule (numero_matricule, marque, nombre_places) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, vehicule.getNumeroMatricule());
            stmt.setString(2, vehicule.getMarque());
            stmt.setInt(3, vehicule.getNombrePlaces());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
