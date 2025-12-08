package com.example.javafx_project;

import java.time.LocalDate;
import java.util.List;

public class Trajet {
    private int id;
    private LocalDate dateTrajet;
    private String heureDepart;
    private String villeDepart;
    private String villeArrivee;
    private String heureArrivee;
    private Vehicule vehicule;
    private int placesRestantes;
    private List<Arret> arrets;
    private List<Prix> prix;
    // Constructeurs, getters et setters
    public Trajet() {}

    public Trajet(LocalDate dateTrajet,String heureDepart, String villeDepart, String villeArrivee, String heureArrivee, Vehicule vehicule, int placesRestantes, List<Arret> arrets, List<Prix> prix) {
        this.dateTrajet = dateTrajet;
        this.heureDepart = heureDepart;
        this.villeDepart = villeDepart;
        this.villeArrivee = villeArrivee;
        this.heureArrivee = heureArrivee;
        this.vehicule = vehicule;
        this.placesRestantes = placesRestantes;
        this.arrets = arrets;
        this.prix = prix;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocalDate getDateTrajet() { return dateTrajet; }
    public void setDateTrajet(LocalDate dateTrajet) { this.dateTrajet = dateTrajet; }
    public String getHeureDepart() { return heureDepart; }
    public void setHeureDepart(String heureDepart) { this.heureDepart = heureDepart; }
    public String getVilleDepart() { return villeDepart; }
    public void setVilleDepart(String villeDepart) { this.villeDepart = villeDepart; }
    public String getVilleArrivee() { return villeArrivee; }
    public void setVilleArrivee(String villeArrivee) { this.villeArrivee = villeArrivee; }
    public String getHeureArrivee() { return heureArrivee; }
    public void setHeureArrivee(String heureArrivee) { this.heureArrivee = heureArrivee; }
    public Vehicule getVehicule() { return vehicule; }
    public void setVehicule(Vehicule vehicule) { this.vehicule = vehicule; }
    public int getPlacesRestantes() { return placesRestantes; }
    public void setPlacesRestantes(int placesRestantes) { this.placesRestantes = placesRestantes; }
    public List<Arret> getArrets() { return arrets; }
    public void setArrets(List<Arret> arrets) { this.arrets = arrets; }
    public List<Prix> getPrix() { return prix; }
    public void setPrix(List<Prix> prix) { this.prix = prix; }

}
