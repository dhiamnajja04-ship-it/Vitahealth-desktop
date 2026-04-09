package com.vitahealth.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Forum {
    private int id;
    private String titre;
    private String slug;
    private String contenu;
    private User auteur;
    private String imageName;
    private LocalDateTime createdAt;
    private List<Commentaire> commentaires = new ArrayList<>();
    private List<User> likes = new ArrayList<>();

    public Forum() {
        this.createdAt = LocalDateTime.now();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) {
        this.titre = titre;
        if (titre != null) {
            this.slug = titre.toLowerCase()
                    .replaceAll("[^a-z0-9\\s]", "")
                    .replaceAll("\\s+", "-");
        }
    }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public User getAuteur() { return auteur; }
    public void setAuteur(User auteur) { this.auteur = auteur; }

    public String getImageName() { return imageName; }
    public void setImageName(String imageName) { this.imageName = imageName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<Commentaire> getCommentaires() { return commentaires; }
    public List<User> getLikes() { return likes; }

    public void addLike(User user) {
        if (!this.likes.contains(user)) {
            this.likes.add(user);
        }
    }

    public void removeLike(User user) {
        this.likes.remove(user);
    }

    public int getLikesCount() { return likes.size(); }
    public int getCommentairesCount() { return commentaires.size(); }
}
