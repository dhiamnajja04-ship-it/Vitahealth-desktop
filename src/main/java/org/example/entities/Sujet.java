package org.example.entities;

import java.sql.Timestamp;

public class Sujet {

    private int id;
    private String titre;
    private String categorie;
    private String contenu;
    private String auteur;
    private boolean valid;
    private Timestamp dateCreation;

    // ── Constructeurs ──────────────────────────────────────────────────────────

    public Sujet() {}

    public Sujet(String titre, String categorie, String contenu, String auteur) {
        this.titre = titre;
        this.categorie = categorie;
        this.contenu = contenu;
        this.auteur = auteur;
        this.valid = false;
        this.dateCreation = new Timestamp(System.currentTimeMillis());
    }

    public Sujet(int id, String titre, String categorie, String contenu,
                 String auteur, boolean valid, Timestamp dateCreation) {
        this.id = id;
        this.titre = titre;
        this.categorie = categorie;
        this.contenu = contenu;
        this.auteur = auteur;
        this.valid = valid;
        this.dateCreation = dateCreation;
    }

    // ── Getters et Setters ─────────────────────────────────────────────────────

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public String getAuteur() { return auteur; }
    public void setAuteur(String auteur) { this.auteur = auteur; }

    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }

    public Timestamp getDateCreation() { return dateCreation; }
    public void setDateCreation(Timestamp dateCreation) { this.dateCreation = dateCreation; }

    @Override
    public String toString() {
        return "Sujet{id=" + id + ", titre='" + titre + "', categorie='" + categorie +
                "', auteur='" + auteur + "', valid=" + valid + ", dateCreation=" + dateCreation + '}';
    }
}