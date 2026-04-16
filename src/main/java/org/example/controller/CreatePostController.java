package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.entities.Sujet;
import org.example.entities.Utilisateur;
import org.example.services.SujetService;

public class CreatePostController {

    @FXML private TextField titreField;
    @FXML private ComboBox<String> categorieCombo;
    @FXML private TextArea contenuField;

    private Utilisateur currentUser;
    private SujetService sujetService = new SujetService();

    @FXML
    public void initialize() {
        categorieCombo.getItems().addAll("Cardiologie", "Neurologie", "Nutrition", "Pédiatrie", "Psychologie", "Général");
        categorieCombo.setValue("Général");
    }

    public void setCurrentUser(Utilisateur user) {
        this.currentUser = user;
    }

    @FXML
    private void closeDialog() {
        titreField.getScene().getWindow().hide();
    }

    @FXML
    private void saveDraft() {
        // Optional: save as draft (not published)
        closeDialog();
    }

    @FXML
    private void publish() {
        String titre = titreField.getText().trim();
        String contenu = contenuField.getText().trim();
        String categorie = categorieCombo.getValue();

        if (titre.isEmpty() || contenu.isEmpty() || categorie == null) {
            showAlert("Veuillez remplir tous les champs.");
            return;
        }

        Sujet sujet = new Sujet(titre, categorie, contenu, currentUser.getId(), currentUser.getNom());
        sujet.setValid(false);  // requires admin validation
        sujetService.ajouter(sujet);

        closeDialog();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
        alert.showAndWait();
    }
}