package com.example.javafx_project;

public class Reservation {
    private int id;
    private int trajetId;
    private String villeDepart;
    private String villeArrivee;
    private String nom;
    private String prenom;
    private String cin;
    private int place;
    // Constructeurs, getters et setters
    public Reservation(int id, int trajetId, String villeDepart, String villeArrivee, String nom, String prenom, String cin, int place) {
        this.id = id;
        this.trajetId = trajetId;
        this.villeDepart = villeDepart;
        this.villeArrivee = villeArrivee;
        this.nom = nom;
        this.prenom = prenom;
        this.cin = cin;
        this.place = place;
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
    public int getTrajetId() {return trajetId;}
    public void setTrajetId(int trajetId) {this.trajetId = trajetId;}
    public String getVilleDepart() { return villeDepart; }
    public void setVilleDepart(String villeDepart) { this.villeDepart = villeDepart; }
    public String getVilleArrivee() { return villeArrivee; }
    public void setVilleArrivee(String villeArrivee) { this.villeArrivee = villeArrivee; }
    public String getNom() {return nom;}
    public void setNom(String nom) {this.nom = nom;}
    public String getPrenom() {return prenom;}
    public void setPrenom(String prenom) {this.prenom = prenom;}
    public String getCin() {return cin;}
    public void setCin(String cin) {this.cin = cin;}
    public int getPlace() {return place;}
    public void setPlace(int place) {this.place = place;}

}
