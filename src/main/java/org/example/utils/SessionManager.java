package org.example.utils;

import org.example.entities.Utilisateur;
import org.example.services.UtilisateurService;

public class SessionManager {
    private static SessionManager instance;
    private Utilisateur currentUser;
    private UtilisateurService utilisateurService = new UtilisateurService();

    private SessionManager() {
        // Par défaut, on charge un utilisateur admin pour les tests (car pas de login)
        // Vous pouvez changer l'ID selon votre base
        currentUser = utilisateurService.getById(1); // Admin
        if (currentUser == null) {
            // Fallback : médecin
            currentUser = utilisateurService.getById(2);
        }
    }

    public static SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }

    public Utilisateur getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(Utilisateur user) {
        this.currentUser = user;
    }

    // Permet de changer d'utilisateur pour tester différents rôles
    public void switchUser(int userId) {
        Utilisateur user = utilisateurService.getById(userId);
        if (user != null) {
            currentUser = user;
        }
    }

    public boolean isAdmin() {
        return currentUser != null && "Admin".equals(currentUser.getRole());
    }

    public boolean isMedecin() {
        return currentUser != null && "Medecin".equals(currentUser.getRole());
    }
}