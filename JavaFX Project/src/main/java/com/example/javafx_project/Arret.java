package com.example.javafx_project;

public class Arret {
    private int id;
    private String ville;
    private String heureArrivee;
    private int tempsReste;  // En minutes
    // Constructeurs, getters et setters
    public Arret() {}
    public Arret(String ville, String heureArrivee, int tempsReste) {
        this.ville = ville;
        this.heureArrivee = heureArrivee;
        this.tempsReste = tempsReste;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }
    public String getHeureArrivee() { return heureArrivee; }
    public void setHeureArrivee(String heureArrivee) { this.heureArrivee = heureArrivee; }
    public int getTempsReste() { return tempsReste; }
    public void setTempsReste(int tempsReste) { this.tempsReste = tempsReste; }

}
