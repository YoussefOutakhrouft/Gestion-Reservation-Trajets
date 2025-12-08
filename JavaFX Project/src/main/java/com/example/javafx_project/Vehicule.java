package com.example.javafx_project;

public class Vehicule {
    private int id;
    private String numeroMatricule;
    private String marque;
    private int nombrePlaces;
    // Constructeurs, getters et setters
    public Vehicule() {}
    public Vehicule(String numeroMatricule, String marque, int nombrePlaces) {
        this.numeroMatricule = numeroMatricule;
        this.marque = marque;
        this.nombrePlaces = nombrePlaces;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNumeroMatricule() { return numeroMatricule; }
    public void setNumeroMatricule(String numeroMatricule) { this.numeroMatricule = numeroMatricule; }
    public String getMarque() { return marque; }
    public void setMarque(String marque) { this.marque = marque; }
    public int getNombrePlaces() { return nombrePlaces; }
    public void setNombrePlaces(int nombrePlaces) { this.nombrePlaces = nombrePlaces; }
    @Override
    public String toString() {
        return numeroMatricule + " - " + marque + " (" + nombrePlaces + " places)";
    }
}
