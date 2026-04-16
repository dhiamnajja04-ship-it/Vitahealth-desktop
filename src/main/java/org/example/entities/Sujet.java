package org.example.entities;

import java.sql.Timestamp;

public class Sujet {
    private int id;
    private String titre;
    private String categorie;
    private String contenu;
    private int auteurId;          // FK vers utilisateur
    private boolean valid;
    private Timestamp dateCreation;
    // Champs pour affichage (non persistés)
    private String auteurNom;
    private String auteurAvatar;

    public Sujet() {}

    public Sujet(String titre, String categorie, String contenu, int auteurId) {
        this(titre, categorie, contenu, auteurId, "");
    }

    public Sujet(String titre, String categorie, String contenu, int auteurId, String auteurNom) {
        this.titre = titre;
        this.categorie = categorie;
        this.contenu = contenu;
        this.auteurId = auteurId;
        this.auteurNom = auteurNom;
        this.valid = false;
        this.dateCreation = new Timestamp(System.currentTimeMillis());
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }
    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }
    public int getAuteurId() { return auteurId; }
    public void setAuteurId(int auteurId) { this.auteurId = auteurId; }
    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }
    public Timestamp getDateCreation() { return dateCreation; }
    public void setDateCreation(Timestamp dateCreation) { this.dateCreation = dateCreation; }
    public String getAuteurNom() { return auteurNom; }
    public void setAuteurNom(String auteurNom) { this.auteurNom = auteurNom; }
    public String getAuteurAvatar() { return auteurAvatar; }
    public void setAuteurAvatar(String auteurAvatar) { this.auteurAvatar = auteurAvatar; }
}