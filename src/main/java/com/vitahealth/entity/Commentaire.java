package com.vitahealth.entity;

import java.time.LocalDateTime;

public class Commentaire {
    private int id;
    private String contenu;
    private Forum forum;
    private User auteur;
    private LocalDateTime createdAt;

    public Commentaire() {
        this.createdAt = LocalDateTime.now();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public Forum getForum() { return forum; }
    public void setForum(Forum forum) { this.forum = forum; }

    public User getAuteur() { return auteur; }
    public void setAuteur(User auteur) { this.auteur = auteur; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
