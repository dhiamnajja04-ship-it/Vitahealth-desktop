package com.vitahealth.entity;

import com.vitahealth.util.Constants;
import java.time.LocalDateTime;

public class Notification {
    private int id;
    private String titre;
    private String message;
    private String type;
    private boolean lue;
    private LocalDateTime dateCreation;
    private LocalDateTime dateLecture;
    private String lien;
    private User user;

    public Notification() {
        this.dateCreation = LocalDateTime.now();
        this.type = Constants.NOTIF_INFO;
        this.lue = false;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public boolean isLue() { return lue; }
    public void setLue(boolean lue) {
        this.lue = lue;
        if (lue && this.dateLecture == null) {
            this.dateLecture = LocalDateTime.now();
        }
    }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDateTime getDateLecture() { return dateLecture; }
    public void setDateLecture(LocalDateTime dateLecture) { this.dateLecture = dateLecture; }

    public String getLien() { return lien; }
    public void setLien(String lien) { this.lien = lien; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public void markAsRead() {
        setLue(true);
    }
}
