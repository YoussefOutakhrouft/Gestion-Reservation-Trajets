package com.example.javafx_project;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class PdfGenerator {
    // Méthode pour générer le billet PDF avec un design moderne
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
            Document document = new Document(pdfDoc);
            document.setMargins(36, 36, 36, 36);  // Marges pour un look professionnel

            // Charger une police (optionnel, utilise Helvetica par défaut)
            PdfFont boldFont = PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA_BOLD);
            PdfFont normalFont = PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA);

            // En-tête avec logo (remplacez par le chemin de votre logo PNG)
            try {
                Image logo = new Image(ImageDataFactory.create("path/to/your/logo.png"));  // Remplacez par le chemin réel
                logo.setWidth(100);  // Taille du logo
                logo.setHeight(50);
                document.add(logo.setTextAlignment(TextAlignment.CENTER));
            } catch (Exception e) {
                // Si pas de logo, ignorer
            }

            // Titre principal
            Paragraph title = new Paragraph("Billet de Réservation")
                    .setFont(boldFont)
                    .setFontSize(24)
                    .setFontColor(ColorConstants.BLUE)  // Couleur bleue professionnelle
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(title);

            // Numéro de billet unique (généré aléatoirement)
            Random random = new Random();
            String numeroBillet = "BILLET-" + String.format("%06d", random.nextInt(999999));
            Paragraph billetNumber = new Paragraph("Numéro de Billet : " + numeroBillet)
                    .setFont(normalFont)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10);
            document.add(billetNumber);

            // Tableau avec les détails (amélioré avec couleurs et bordures)
            Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2}));
            table.setWidth(UnitValue.createPercentValue(100));
            table.setMarginBottom(20);

            // Styles pour les cellules d'en-tête et de données (réutilisables)
            Cell headerCellStyle = new Cell().setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setBorder(new SolidBorder(ColorConstants.GRAY, 1))
                    .setPadding(5)
                    .setFont(boldFont)
                    .setFontSize(12);
            Cell dataCellStyle = new Cell().setBorder(new SolidBorder(ColorConstants.GRAY, 1))
                    .setPadding(5)
                    .setFont(normalFont)
                    .setFontSize(11);

            // Informations du réserveur
            table.addCell(new Cell().setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setBorder(new SolidBorder(ColorConstants.GRAY, 1))
                    .setPadding(5)
                    .setFont(boldFont)
                    .setFontSize(12)
                    .add(new Paragraph("Nom")));
            table.addCell(new Cell().setBorder(new SolidBorder(ColorConstants.GRAY, 1))
                    .setPadding(5)
                    .setFont(normalFont)
                    .setFontSize(11)
                    .add(new Paragraph(reservation.getNom())));
            table.addCell(new Cell().setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setBorder(new SolidBorder(ColorConstants.GRAY, 1))
                    .setPadding(5)
                    .setFont(boldFont)
                    .setFontSize(12)
                    .add(new Paragraph("Prénom")));
            table.addCell(new Cell().setBorder(new SolidBorder(ColorConstants.GRAY, 1))
                    .setPadding(5)
                    .setFont(normalFont)
                    .setFontSize(11)
                    .add(new Paragraph(reservation.getPrenom())));
            table.addCell(new Cell().setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setBorder(new SolidBorder(ColorConstants.GRAY, 1))
                    .setPadding(5)
                    .setFont(boldFont)
                    .setFontSize(12)
                    .add(new Paragraph("CIN")));
            table.addCell(new Cell().setBorder(new SolidBorder(ColorConstants.GRAY, 1))
                    .setPadding(5)
                    .setFont(normalFont)
                    .setFontSize(11)
                    .add(new Paragraph(reservation.getCin())));
            table.addCell(new Cell().setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setBorder(new SolidBorder(ColorConstants.GRAY, 1))
                    .setPadding(5)
                    .setFont(boldFont)
                    .setFontSize(12)
                    .add(new Paragraph("Place")));
            table.addCell(new Cell().setBorder(new SolidBorder(ColorConstants.GRAY, 1))
                    .setPadding(5)
                    .setFont(normalFont)
                    .setFontSize(11)
                    .add(new Paragraph(String.valueOf(reservation.getPlace()))));

            // Informations du segment
            table.addCell(new Cell().setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setBorder(new SolidBorder(ColorConstants.GRAY, 1))
                    .setPadding(5)
                    .setFont(boldFont)
                    .setFontSize(12)
                    .add(new Paragraph("Ville de Départ")));
            table.addCell(new Cell().setBorder(new SolidBorder(ColorConstants.GRAY, 1))
                    .setPadding(5)
                    .setFont(normalFont)
                    .setFontSize(11)
                    .add(new Paragraph(segment.getVilleDepart())));
            table.addCell(new Cell().setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setBorder(new SolidBorder(ColorConstants.GRAY, 1))
                    .setPadding(5)
                    .setFont(boldFont)
                    .setFontSize(12)
                    .add(new Paragraph("Ville d'Arrivée")));
            table.addCell(new Cell().setBorder(new SolidBorder(ColorConstants.GRAY, 1))
                    .setPadding(5)
                    .setFont(normalFont)
                    .setFontSize(11)
                    .add(new Paragraph(segment.getVilleArrivee())));
            table.addCell(new Cell().setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setBorder(new SolidBorder(ColorConstants.GRAY, 1))
                    .setPadding(5)
                    .setFont(boldFont)
                    .setFontSize(12)
                    .add(new Paragraph("Prix")));
            table.addCell(new Cell().setBorder(new SolidBorder(ColorConstants.GRAY, 1))
                    .setPadding(5)
                    .setFont(normalFont)
                    .setFontSize(11)
                    .add(new Paragraph(segment.getPrix() + " DH")));

            // Informations du trajet
            table.addCell(new Cell().setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setBorder(new SolidBorder(ColorConstants.GRAY, 1))
                    .setPadding(5)
                    .setFont(boldFont)
                    .setFontSize(12)
                    .add(new Paragraph("Date du Trajet")));
            table.addCell(new Cell().setBorder(new SolidBorder(ColorConstants.GRAY, 1))
                    .setPadding(5)
                    .setFont(normalFont)
                    .setFontSize(11)
                    .add(new Paragraph(trajet.getDateTrajet().toString())));
            table.addCell(new Cell().setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setBorder(new SolidBorder(ColorConstants.GRAY, 1))
                    .setPadding(5)
                    .setFont(boldFont)
                    .setFontSize(12)
                    .add(new Paragraph("Heure de Départ")));
            table.addCell(new Cell().setBorder(new SolidBorder(ColorConstants.GRAY, 1))
                    .setPadding(5)
                    .setFont(normalFont)
                    .setFontSize(11)
                    .add(new Paragraph(trajet.getHeureDepart())));
            table.addCell(new Cell().setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setBorder(new SolidBorder(ColorConstants.GRAY, 1))
                    .setPadding(5)
                    .setFont(boldFont)
                    .setFontSize(12)
                    .add(new Paragraph("Heure d'Arrivée")));
            table.addCell(new Cell().setBorder(new SolidBorder(ColorConstants.GRAY, 1))
                    .setPadding(5)
                    .setFont(normalFont)
                    .setFontSize(11)
                    .add(new Paragraph(trajet.getHeureArrivee())));
            table.addCell(new Cell().setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setBorder(new SolidBorder(ColorConstants.GRAY, 1))
                    .setPadding(5)
                    .setFont(boldFont)
                    .setFontSize(12)
                    .add(new Paragraph("Véhicule (Matricule)")));
            table.addCell(new Cell().setBorder(new SolidBorder(ColorConstants.GRAY, 1))
                    .setPadding(5)
                    .setFont(normalFont)
                    .setFontSize(11)
                    .add(new Paragraph(trajet.getVehicule().getNumeroMatricule())));
            table.addCell(new Cell().setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setBorder(new SolidBorder(ColorConstants.GRAY, 1))
                    .setPadding(5)
                    .setFont(boldFont)
                    .setFontSize(12)
                    .add(new Paragraph("Marque du Véhicule")));
            table.addCell(new Cell().setBorder(new SolidBorder(ColorConstants.GRAY, 1))
                    .setPadding(5)
                    .setFont(normalFont)
                    .setFontSize(11)
                    .add(new Paragraph(trajet.getVehicule().getMarque())));

            document.add(table);

            // Code QR simulé (remplacez par une vraie génération QR si nécessaire)
            Paragraph qrPlaceholder = new Paragraph("Code QR : [Simulé - Scannez pour vérifier]")
                    .setFont(normalFont)
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20)
                    .setBorder(new SolidBorder(ColorConstants.BLACK, 1))
                    .setPadding(10);
            document.add(qrPlaceholder);

            // Pied de page
            Paragraph footer = new Paragraph("Merci pour votre réservation !\n" +
                    "Pour toute question, contactez-nous : support@transportcompany.com | +212 123 456 789\n" +
                    "Conditions : Ce billet est non remboursable. Présentez-le à l'embarquement.")
                    .setFont(normalFont)
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.GRAY)
                    .setMarginTop(20);
            document.add(footer);

            document.close();
            System.out.println("Billet PDF généré avec succès : " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la génération du PDF.");
        }
    }
}
