package com.vitahealth.entity;

import com.vitahealth.util.Constants;
import java.time.LocalDateTime;

public class Reclamation {
    private int id;
    private String titre;
    private String description;
    private String statut;
    private LocalDateTime dateCreation;
    private LocalDateTime dateResolution;
    private String reponse;
    private User user;

    public Reclamation() {
        this.dateCreation = LocalDateTime.now();
        this.statut = Constants.RECLAMATION_EN_ATTENTE;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDateTime getDateResolution() { return dateResolution; }
    public void setDateResolution(LocalDateTime dateResolution) { this.dateResolution = dateResolution; }

    public String getReponse() { return reponse; }
    public void setReponse(String reponse) { this.reponse = reponse; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public boolean isResolue() { return Constants.RECLAMATION_RESOLUE.equals(statut); }
    public boolean isEnAttente() { return Constants.RECLAMATION_EN_ATTENTE.equals(statut); }
    public boolean isEnCours() { return Constants.RECLAMATION_EN_COURS.equals(statut); }

    public void marquerEnCours() { this.statut = Constants.RECLAMATION_EN_COURS; }

    public void marquerResolue(String reponse) {
        this.statut = Constants.RECLAMATION_RESOLUE;
        this.reponse = reponse;
        this.dateResolution = LocalDateTime.now();
    }

    public void marquerRejetee(String reponse) {
        this.statut = Constants.RECLAMATION_REJETEE;
        this.reponse = reponse;
        this.dateResolution = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Réclamation #" + id + " - " + titre + " (" + statut + ")";
    }
}
