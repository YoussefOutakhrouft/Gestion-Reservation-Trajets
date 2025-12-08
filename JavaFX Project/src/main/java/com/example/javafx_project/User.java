package com.example.javafx_project;

public class User {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String motDePass; // Stocké hashé
    private String role; // "ADMIN" ou "GEST"
    // Constructeurs
    public User() {}
    public User(String nom, String prenom, String email, String motDePass, String role) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePass = motDePass;
        this.role = role;
    }
    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMotDePass() { return motDePass; }
    public void setMotDePass(String motDePass) { this.motDePass = motDePass; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    @Override
    public String toString() {
        return "User{id=" + id + ", nom='" + nom + "', prenom='" + prenom + "', email='" + email + "', role='" + role + "'}";
    }
}
