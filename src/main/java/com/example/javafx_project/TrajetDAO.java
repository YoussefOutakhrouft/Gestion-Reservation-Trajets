package com.example.javafx_project;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TrajetDAO {
    public void insertTrajet(Trajet trajet) {
        String query = "INSERT INTO Trajet (date_trajet, heure_depart, ville_depart, ville_arrivee, heure_arrivee, vehicule_id, places_restantes) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDate(1, Date.valueOf(trajet.getDateTrajet()));
            stmt.setString(2, trajet.getHeureDepart());
            stmt.setString(3, trajet.getVilleDepart());
            stmt.setString(4, trajet.getVilleArrivee());
            stmt.setString(5, trajet.getHeureArrivee());
            stmt.setInt(6, trajet.getVehicule().getId());
            stmt.setInt(7, trajet.getPlacesRestantes());
            stmt.executeUpdate();
            // Récupérer l'ID généré du trajet
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int trajetId = rs.getInt(1);
                // Insérer les arrêts
                for (Arret arret : trajet.getArrets()) {
                    insertArret(trajetId, arret);
                }
                new PrixDAO().insertPrix(trajetId, trajet.getPrix());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Trajet> getAllTrajets() {
        List<Trajet> trajets = new ArrayList<>();
        String query = "SELECT t.*, v.numero_matricule, v.marque, v.nombre_places FROM Trajet t JOIN Vehicule v ON t.vehicule_id = v.id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Trajet trajet = new Trajet();
                trajet.setId(rs.getInt("id"));
                trajet.setDateTrajet(rs.getDate("date_trajet").toLocalDate());
                trajet.setHeureDepart(rs.getString("heure_depart"));
                trajet.setVilleDepart(rs.getString("ville_depart"));
                trajet.setVilleArrivee(rs.getString("ville_arrivee"));
                trajet.setHeureArrivee(rs.getString("heure_arrivee"));
                trajet.setPlacesRestantes(rs.getInt("places_restantes"));
                Vehicule vehicule = new Vehicule();
                vehicule.setId(rs.getInt("vehicule_id"));
                vehicule.setNumeroMatricule(rs.getString("numero_matricule"));
                vehicule.setMarque(rs.getString("marque"));
                vehicule.setNombrePlaces(rs.getInt("nombre_places"));
                trajet.setVehicule(vehicule);

                trajets.add(trajet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trajets;
    }

    public void deleteTrajet(int id) {
        String query = "DELETE FROM Trajet WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTrajet(Trajet trajet) {
        String query = "UPDATE Trajet SET date_trajet = ?, heure_depart = ?, ville_depart = ?, ville_arrivee = ?, heure_arrivee = ?, vehicule_id = ?, places_restantes = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDate(1, Date.valueOf(trajet.getDateTrajet()));
            stmt.setString(2, trajet.getHeureDepart());
            stmt.setString(3, trajet.getVilleDepart());
            stmt.setString(4, trajet.getVilleArrivee());
            stmt.setString(5, trajet.getHeureArrivee());
            stmt.setInt(6, trajet.getVehicule().getId());
            stmt.setInt(7, trajet.getPlacesRestantes());
            stmt.setInt(8, trajet.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isVehiculeAvailable(int vehiculeId, LocalDate date, String heureDepart, String heureArrivee, Integer excludeTrajetId) {
        String query = "SELECT heure_depart, heure_arrivee FROM Trajet WHERE vehicule_id = ? AND date_trajet = ?";
        if (excludeTrajetId != null) {
            query += " AND id != ?";
        }
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, vehiculeId);
            stmt.setDate(2, Date.valueOf(date));
            if (excludeTrajetId != null) {
                stmt.setInt(3, excludeTrajetId);
            }
            ResultSet rs = stmt.executeQuery();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
            LocalTime newDepart = LocalTime.parse(heureDepart, formatter);
            LocalTime newArrivee = LocalTime.parse(heureArrivee, formatter);
            while (rs.next()) {
                LocalTime existingDepart = LocalTime.parse(rs.getString("heure_depart"), formatter);
                LocalTime existingArrivee = LocalTime.parse(rs.getString("heure_arrivee"), formatter);
                // Vérifier le chevauchement : départ nouveau < arrivée existant && départ existant < arrivée nouveau
                if (newDepart.isBefore(existingArrivee) && existingDepart.isBefore(newArrivee)) {
                    return false;  // Conflit trouvé
                }
            }
        } catch (SQLException | java.time.format.DateTimeParseException e) {
            e.printStackTrace();
        }
        return true;  // Disponible
    }

    private void insertArret(int trajetId, Arret arret) {
        String query = "INSERT INTO Arret (trajet_id, ville, heure_arrivee, temps_reste) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, trajetId);
            stmt.setString(2, arret.getVille());
            stmt.setString(3, arret.getHeureArrivee());
            stmt.setInt(4, arret.getTempsReste());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
