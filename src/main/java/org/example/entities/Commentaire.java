package org.example.entities;

import java.sql.Timestamp;

public class Commentaire {

    private int id;
    private String contenu;
    private String auteur;
    private Timestamp dateCreation;
    private int sujetId;

    public Commentaire() {}

    public Commentaire(String contenu, String auteur, int sujetId) {
        this.contenu = contenu;
        this.auteur = auteur;
        this.sujetId = sujetId;
        this.dateCreation = new Timestamp(System.currentTimeMillis());
    }

    public Commentaire(int id, String contenu, String auteur, Timestamp dateCreation, int sujetId) {
        this.id = id;
        this.contenu = contenu;
        this.auteur = auteur;
        this.dateCreation = dateCreation;
        this.sujetId = sujetId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public String getAuteur() { return auteur; }
    public void setAuteur(String auteur) { this.auteur = auteur; }

    public Timestamp getDateCreation() { return dateCreation; }
    public void setDateCreation(Timestamp dateCreation) { this.dateCreation = dateCreation; }

    public int getSujetId() { return sujetId; }
    public void setSujetId(int sujetId) { this.sujetId = sujetId; }

    @Override
    public String toString() {
        return "Commentaire{id=" + id + ", auteur='" + auteur + "', sujetId=" + sujetId + "}";
    }
}