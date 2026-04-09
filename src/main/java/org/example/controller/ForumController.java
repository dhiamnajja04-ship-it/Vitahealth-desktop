package org.example.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import org.example.entities.Commentaire;
import org.example.entities.Sujet;
import org.example.services.CommentaireService;
import org.example.services.SujetService;

import java.net.URL;
import java.sql.Timestamp;
import java.util.List;
import java.util.ResourceBundle;

public class ForumController implements Initializable {

    // ── Sujets ────────────────────────────────────────────────────────────────
    @FXML private TextField        titreField;
    @FXML private ComboBox<String> categorieCombo;
    @FXML private TextField        auteurField;
    @FXML private TextArea         contenuArea;
    @FXML private TextField        searchField;

    @FXML private TableView<Sujet>              sujetTable;
    @FXML private TableColumn<Sujet, String>    colTitre;
    @FXML private TableColumn<Sujet, String>    colCategorie;
    @FXML private TableColumn<Sujet, String>    colAuteur;
    @FXML private TableColumn<Sujet, Boolean>   colValid;
    @FXML private TableColumn<Sujet, Timestamp> colDate;

    // ── Commentaires ──────────────────────────────────────────────────────────
    @FXML private TableView<Commentaire>              commentaireTable;
    @FXML private TableColumn<Commentaire, String>    colComAuteur;
    @FXML private TableColumn<Commentaire, String>    colComContenu;
    @FXML private TableColumn<Commentaire, Timestamp> colComDate;

    @FXML private TextField comAuteurField;
    @FXML private TextArea  comContenuArea;
    @FXML private Label     sujetSelectionneLabel;
    @FXML private Label     nbCommentairesLabel;
    @FXML private Label     statusLabel;

    // ── Services ──────────────────────────────────────────────────────────────
    private final SujetService       sujetService       = new SujetService();
    private final CommentaireService commentaireService = new CommentaireService();

    private Sujet       sujetSelectionne       = null;
    private Commentaire commentaireSelectionne = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Colonnes sujets
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colCategorie.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        colAuteur.setCellValueFactory(new PropertyValueFactory<>("auteur"));
        colValid.setCellValueFactory(new PropertyValueFactory<>("valid"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateCreation"));

        // Colonnes commentaires
        colComAuteur.setCellValueFactory(new PropertyValueFactory<>("auteur"));
        colComContenu.setCellValueFactory(new PropertyValueFactory<>("contenu"));
        colComDate.setCellValueFactory(new PropertyValueFactory<>("dateCreation"));

        // Catégories
        categorieCombo.setItems(FXCollections.observableArrayList(
                "Nutrition", "Cardiologie", "Psychologie", "Conseils généraux", "Autre"
        ));

        // Sélection commentaire
        commentaireTable.setOnMouseClicked(e ->
                commentaireSelectionne = commentaireTable.getSelectionModel().getSelectedItem()
        );

        chargerSujets();
    }

    // ── Charger sujets ────────────────────────────────────────────────────────
    private void chargerSujets() {
        List<Sujet> sujets = sujetService.getAll();
        sujetTable.setItems(FXCollections.observableArrayList(sujets));
        setStatus("✅ " + sujets.size() + " sujet(s) chargé(s)");
    }

    // ── Sélectionner sujet ────────────────────────────────────────────────────
    @FXML
    void selectionnerSujet(MouseEvent event) {
        sujetSelectionne = sujetTable.getSelectionModel().getSelectedItem();
        if (sujetSelectionne == null) return;

        titreField.setText(sujetSelectionne.getTitre());
        categorieCombo.setValue(sujetSelectionne.getCategorie());
        auteurField.setText(sujetSelectionne.getAuteur());
        contenuArea.setText(sujetSelectionne.getContenu());

        chargerCommentaires(sujetSelectionne.getId());
        sujetSelectionneLabel.setText("💬 " + sujetSelectionne.getTitre());
    }

    private void chargerCommentaires(int sujetId) {
        commentaireTable.setItems(
                FXCollections.observableArrayList(commentaireService.getBySujet(sujetId))
        );
        nbCommentairesLabel.setText("Total : " + commentaireService.countBySujet(sujetId) + " commentaire(s)");
    }

    // ── CRUD Sujets ───────────────────────────────────────────────────────────
    @FXML void ajouterSujet() {
        if (!validerFormulaire()) return;
        sujetService.ajouter(new Sujet(
                titreField.getText(), categorieCombo.getValue(),
                contenuArea.getText(), auteurField.getText()
        ));
        effacer(); chargerSujets();
        setStatus("✅ Sujet ajouté !");
    }

    @FXML void modifierSujet() {
        if (sujetSelectionne == null) { alerte("Sélectionnez un sujet."); return; }
        if (!validerFormulaire()) return;
        sujetSelectionne.setTitre(titreField.getText());
        sujetSelectionne.setCategorie(categorieCombo.getValue());
        sujetSelectionne.setAuteur(auteurField.getText());
        sujetSelectionne.setContenu(contenuArea.getText());
        sujetService.modifier(sujetSelectionne);
        chargerSujets();
        setStatus("✅ Sujet modifié !");
    }

    @FXML void supprimerSujet() {
        if (sujetSelectionne == null) { alerte("Sélectionnez un sujet."); return; }
        Alert c = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer « " + sujetSelectionne.getTitre() + " » ?", ButtonType.YES, ButtonType.NO);
        c.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                sujetService.supprimer(sujetSelectionne.getId());
                effacer(); chargerSujets();
                commentaireTable.getItems().clear();
                setStatus("🗑️ Sujet supprimé.");
            }
        });
    }

    @FXML void validerSujet() {
        if (sujetSelectionne == null) { alerte("Sélectionnez un sujet."); return; }
        sujetService.valider(sujetSelectionne.getId(), true);
        chargerSujets(); setStatus("✅ Sujet validé !");
    }

    @FXML void invaliderSujet() {
        if (sujetSelectionne == null) { alerte("Sélectionnez un sujet."); return; }
        sujetService.valider(sujetSelectionne.getId(), false);
        chargerSujets(); setStatus("❌ Sujet invalidé.");
    }

    @FXML void rechercher() {
        String mot = searchField.getText().trim();
        if (mot.isEmpty()) { chargerSujets(); return; }
        List<Sujet> res = sujetService.rechercher(mot);
        sujetTable.setItems(FXCollections.observableArrayList(res));
        setStatus("🔍 " + res.size() + " résultat(s)");
    }

    @FXML void effacer() {
        titreField.clear(); auteurField.clear(); contenuArea.clear();
        categorieCombo.setValue(null); sujetSelectionne = null;
        sujetTable.getSelectionModel().clearSelection();
    }

    // ── Commentaires ──────────────────────────────────────────────────────────
    @FXML void ajouterCommentaire() {
        if (sujetSelectionne == null) { alerte("Sélectionnez un sujet."); return; }
        if (comAuteurField.getText().isEmpty() || comContenuArea.getText().isEmpty()) {
            alerte("Remplissez auteur et commentaire."); return;
        }
        commentaireService.ajouter(new Commentaire(
                comContenuArea.getText(), comAuteurField.getText(), sujetSelectionne.getId()
        ));
        comAuteurField.clear(); comContenuArea.clear();
        chargerCommentaires(sujetSelectionne.getId());
        setStatus("✅ Commentaire ajouté !");
    }

    @FXML void supprimerCommentaire() {
        if (commentaireSelectionne == null) { alerte("Sélectionnez un commentaire."); return; }
        commentaireService.supprimer(commentaireSelectionne.getId());
        chargerCommentaires(sujetSelectionne.getId());
        setStatus("🗑️ Commentaire supprimé.");
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private boolean validerFormulaire() {
        if (titreField.getText().isEmpty() || auteurField.getText().isEmpty()
                || contenuArea.getText().isEmpty() || categorieCombo.getValue() == null) {
            alerte("Veuillez remplir tous les champs."); return false;
        }
        return true;
    }

    private void alerte(String msg) {
        new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK).showAndWait();
    }

    private void setStatus(String msg) { statusLabel.setText(msg); }
}