package com.example.javafx_project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    public static class Segment {
        private String villeDepart;
        private String villeArrivee;
        private double prix;
        public Segment(String villeDepart, String villeArrivee, double prix) {
            this.villeDepart = villeDepart;
            this.villeArrivee = villeArrivee;
            this.prix = prix;
        }
        // Getters
        public String getVilleDepart() { return villeDepart; }
        public String getVilleArrivee() { return villeArrivee; }
        public double getPrix() { return prix; }
        @Override
        public String toString() {
            return villeDepart + " -> " + villeArrivee + " (Prix: " + prix + " DH)";
        }
    }

    // Méthode pour récupérer les segments disponibles pour un trajet (depuis Prix)
    public List<Segment> getSegmentsForTrajet(int trajetId) {
        List<Segment> segments = new ArrayList<>();
        String query = "SELECT ville_depart, ville_arrivee, prix FROM Prix WHERE trajet_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, trajetId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                segments.add(new Segment(
                        rs.getString("ville_depart"),
                        rs.getString("ville_arrivee"),
                        rs.getDouble("prix")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return segments;
    }

    public boolean reserverBillet(int trajetId, String villeDepart, String villeArrivee, String nom, String prenom, String cin, int place) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;
        try {
            // Vérifier si le segment existe dans Prix
            String checkSegmentQuery = "SELECT prix FROM Prix WHERE trajet_id = ? AND ville_depart = ? AND ville_arrivee = ?";
            PreparedStatement checkSegmentStmt = conn.prepareStatement(checkSegmentQuery);
            checkSegmentStmt.setInt(1, trajetId);
            checkSegmentStmt.setString(2, villeDepart);
            checkSegmentStmt.setString(3, villeArrivee);
            ResultSet rsSegment = checkSegmentStmt.executeQuery();
            if (!rsSegment.next()) {
                System.out.println("Segment inexistant.");
                return false;
            }
            // Vérifier si des places sont disponibles (basé sur le véhicule)
            String checkPlacesQuery = "SELECT t.places_restantes FROM Trajet t JOIN Vehicule v ON t.vehicule_id = v.id WHERE t.id = ?";
            PreparedStatement checkPlacesStmt = conn.prepareStatement(checkPlacesQuery);
            checkPlacesStmt.setInt(1, trajetId);
            ResultSet rsPlaces = checkPlacesStmt.executeQuery();
            if (!rsPlaces.next() || rsPlaces.getInt("places_restantes") <= 0) {
                System.out.println("Aucune place disponible.");
                return false;
            }
            // Vérifier si la place est déjà prise pour ce segment
            String checkPlaceQuery = "SELECT id FROM Reservation WHERE trajet_id = ? AND ville_depart = ? AND ville_arrivee = ? AND place = ?";
            PreparedStatement checkPlaceStmt = conn.prepareStatement(checkPlaceQuery);
            checkPlaceStmt.setInt(1, trajetId);
            checkPlaceStmt.setString(2, villeDepart);
            checkPlaceStmt.setString(3, villeArrivee);
            checkPlaceStmt.setInt(4, place);
            if (checkPlaceStmt.executeQuery().next()) {
                System.out.println("Place déjà réservée pour ce segment.");
                return false;
            }
            // Insérer la réservation
            String insertQuery = "INSERT INTO Reservation (trajet_id, ville_depart, ville_arrivee, nom, prenom, cin, place) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setInt(1, trajetId);
            insertStmt.setString(2, villeDepart);
            insertStmt.setString(3, villeArrivee);
            insertStmt.setString(4, nom);
            insertStmt.setString(5, prenom);
            insertStmt.setString(6, cin);
            insertStmt.setInt(7, place);
            insertStmt.executeUpdate();
            // Mettre à jour les places restantes
            String updateQuery = "UPDATE Trajet SET places_restantes = places_restantes - 1 WHERE id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
            updateStmt.setInt(1, trajetId);
            updateStmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    public List<Reservation> getAllReservations() {
        List<Reservation> reservations = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return reservations;
        try {
            String query = "SELECT * FROM Reservation";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                reservations.add(new Reservation(
                        rs.getInt("id"),
                        rs.getInt("trajet_id"),
                        rs.getString("ville_depart"),
                        rs.getString("ville_arrivee"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("cin"),
                        rs.getInt("place")
                ));
            }
        }catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        return reservations;
    }

    public List<Trajet> searchTrajets(String villeDepart, String villeArrivee, String date) {
        TrajetDAO trajetDAO = new TrajetDAO();
        return trajetDAO.searchTrajets(villeDepart, villeArrivee, date);
    }

    public Segment getSegmentForReservation(int trajetId, String villeDepart, String villeArrivee) {
        List<Segment> segments = getSegmentsForTrajet(trajetId);
        for (Segment segment : segments) {
            if (segment.getVilleDepart().equals(villeDepart) && segment.getVilleArrivee().equals(villeArrivee)) {
                return segment;
            }
        }
        return null;  // Retourner null si le segment n'est pas trouvé
    }

}
