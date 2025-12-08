package com.example.javafx_project;

public class Prix {
    private int id;
    private String villeDepart;
    private String villeArrivee;
    private double prix;
    // Constructeurs, getters et setters
    public Prix() {}
    public Prix(String villeDepart, String villeArrivee, double prix) {
        this.villeDepart = villeDepart;
        this.villeArrivee = villeArrivee;
        this.prix = prix;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getVilleDepart() { return villeDepart; }
    public void setVilleDepart(String villeDepart) { this.villeDepart = villeDepart; }
    public String getVilleArrivee() { return villeArrivee; }
    public void setVilleArrivee(String villeArrivee) { this.villeArrivee = villeArrivee; }
    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }
}
