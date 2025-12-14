package com.example.javafx_project;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.File;
import java.io.IOException;

public class PdfGenerator {
    // Méthode pour générer le billet PDF
    public static void genererBilletPdf(Reservation reservation, Trajet trajet, ReservationDAO.Segment segment) {
        // Utiliser JavaFX FileChooser pour sélectionner le chemin
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Sauvegarder le Billet PDF");
        fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
        File file = fileChooser.showSaveDialog(null);  // Passez une Stage si disponible
        if (file == null) {
            System.out.println("Sauvegarde annulée.");
            return;
        }

        try {
            // Créer le PDF
            PdfWriter writer = new PdfWriter(file.getAbsolutePath());
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);  // Correct : com.itextpdf.layout.Document
            // Titre
            document.add(new Paragraph("Billet de Réservation")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(20)
                    .setBold());
            // Tableau avec les détails
            Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2}));  // Correct : com.itextpdf.layout.element.Table
            table.setWidth(UnitValue.createPercentValue(100));

            // Informations du réserveur
            table.addCell("Nom");
            table.addCell(reservation.getNom());
            table.addCell("Prénom");
            table.addCell(reservation.getPrenom());
            table.addCell("CIN");
            table.addCell(reservation.getCin());
            table.addCell("Place");
            table.addCell(String.valueOf(reservation.getPlace()));
            // Informations du segment
            table.addCell("Ville de Départ");
            table.addCell(segment.getVilleDepart());
            table.addCell("Ville d'Arrivée");
            table.addCell(segment.getVilleArrivee());
            table.addCell("Prix");
            table.addCell(segment.getPrix() + " DH");

            // Informations du trajet
            table.addCell("Date du Trajet");
            table.addCell(trajet.getDateTrajet().toString());
            table.addCell("Heure de Départ");
            table.addCell(trajet.getHeureDepart());
            table.addCell("Heure d'Arrivée");
            table.addCell(trajet.getHeureArrivee());
            table.addCell("Véhicule (Matricule)");
            table.addCell(trajet.getVehicule().getNumeroMatricule());
            table.addCell("Marque du Véhicule");
            table.addCell(trajet.getVehicule().getMarque());
            document.add(table);
            // Pied de page
            document.add(new Paragraph("\nMerci pour votre réservation !")
                    .setTextAlignment(TextAlignment.CENTER));
            document.close();
            System.out.println("Billet PDF généré avec succès : " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la génération du PDF.");
        }
    }
}
