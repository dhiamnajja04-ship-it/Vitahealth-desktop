package org.example.entities;

import java.sql.Timestamp;

public class Commentaire {
    private int id;
    private String contenu;
    private int auteurId;
    private Timestamp dateCreation;
    private int sujetId;
    private String auteurNom;      // pour affichage
    private String auteurAvatar;

    public Commentaire() {}

    public Commentaire(String contenu, int auteurId, int sujetId) {
        this(contenu, auteurId, sujetId, "");
    }

    public Commentaire(String contenu, int auteurId, int sujetId, String auteurNom) {
        this.contenu = contenu;
        this.auteurId = auteurId;
        this.sujetId = sujetId;
        this.auteurNom = auteurNom;
        this.dateCreation = new Timestamp(System.currentTimeMillis());
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }
    public int getAuteurId() { return auteurId; }
    public void setAuteurId(int auteurId) { this.auteurId = auteurId; }
    public Timestamp getDateCreation() { return dateCreation; }
    public void setDateCreation(Timestamp dateCreation) { this.dateCreation = dateCreation; }
    public int getSujetId() { return sujetId; }
    public void setSujetId(int sujetId) { this.sujetId = sujetId; }
    public String getAuteurNom() { return auteurNom; }
    public void setAuteurNom(String auteurNom) { this.auteurNom = auteurNom; }
    public String getAuteurAvatar() { return auteurAvatar; }
    public void setAuteurAvatar(String auteurAvatar) { this.auteurAvatar = auteurAvatar; }
}