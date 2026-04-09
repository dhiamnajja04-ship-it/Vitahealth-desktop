package com.vitahealth.controllers;

import com.vitahealth.entity.MedicalRecord;
import com.vitahealth.entity.ParaMedical;
import com.vitahealth.entity.Prescription;
import com.vitahealth.services.ParaMedicalService;
import com.vitahealth.services.PrescriptionService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PatientController {

    // ============ CHAMPS DU FORMULAIRE ============
    @FXML private TextField poidsField;
    @FXML private TextField tailleField;
    @FXML private TextField glycemieField;
    @FXML private TextField tensionField;
    @FXML private Label imcLabel;
    @FXML private Label interpretationLabel;

    // ============ TABLEAU PARAMÈTRES MÉDICAUX ============
    @FXML private TableView<ParaMedical> parametreTable;
    @FXML private TableColumn<ParaMedical, LocalDateTime> colDate;
    @FXML private TableColumn<ParaMedical, Double> colPoids;
    @FXML private TableColumn<ParaMedical, Double> colTaille;
    @FXML private TableColumn<ParaMedical, Double> colGlycemie;
    @FXML private TableColumn<ParaMedical, String> colTension;
    @FXML private TableColumn<ParaMedical, Double> colImc;

    // ============ TABLEAU PRESCRIPTIONS ============
    @FXML private TableView<Prescription> prescriptionTable;
    @FXML private TableColumn<Prescription, LocalDateTime> colPrescDate;
    @FXML private TableColumn<Prescription, String> colMedicaments;
    @FXML private TableColumn<Prescription, String> colDuree;
    @FXML private TableColumn<Prescription, String> colInstructions;

    // ============ SERVICES ============
    private ParaMedicalService paraMedicalService;
    private PrescriptionService prescriptionService;

    // ============ DONNÉES ============
    private MedicalRecord currentMedicalRecord;
    private ObservableList<ParaMedical> parametresList;
    private ObservableList<Prescription> prescriptionsList;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ============ INITIALISATION ============
    @FXML
    public void initialize() {
        // Initialisation des services
        paraMedicalService = new ParaMedicalService();
        prescriptionService = new PrescriptionService();

        // Initialisation des listes
        parametresList = FXCollections.observableArrayList();
        prescriptionsList = FXCollections.observableArrayList();

        // Configuration du tableau des paramètres médicaux
        colDate.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        colPoids.setCellValueFactory(new PropertyValueFactory<>("poids"));
        colTaille.setCellValueFactory(new PropertyValueFactory<>("taille"));
        colGlycemie.setCellValueFactory(new PropertyValueFactory<>("glycemie"));
        colTension.setCellValueFactory(new PropertyValueFactory<>("tensionSystolique"));
        colImc.setCellValueFactory(new PropertyValueFactory<>("imc"));

        // Formatage de la date
        colDate.setCellFactory(column -> new TableCell<ParaMedical, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(formatter));
                }
            }
        });

        // Configuration du tableau des prescriptions
        colPrescDate.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        colMedicaments.setCellValueFactory(new PropertyValueFactory<>("medicationList"));
        colDuree.setCellValueFactory(new PropertyValueFactory<>("duration"));
        colInstructions.setCellValueFactory(new PropertyValueFactory<>("instructions"));

        // Formatage de la date pour les prescriptions
        colPrescDate.setCellFactory(column -> new TableCell<Prescription, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(formatter));
                }
            }
        });

        // Simulation du MedicalRecord (à remplacer par l'ID du patient connecté)
        currentMedicalRecord = new MedicalRecord();
        currentMedicalRecord.setId(1); // À modifier selon votre authentification

        // Calcul de l'IMC en temps réel
        poidsField.textProperty().addListener((obs, old, newVal) -> calculerIMC());
        tailleField.textProperty().addListener((obs, old, newVal) -> calculerIMC());

        // Chargement des données
        rafraichirTableaux();

        // Sélection dans le tableau des paramètres
        parametreTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        remplirChamps(newSelection);
                    }
                }
        );
    }

    // ============ CALCUL IMC ============
    private void calculerIMC() {
        try {
            String poidsText = poidsField.getText();
            String tailleText = tailleField.getText();

            if (poidsText != null && tailleText != null && !poidsText.isEmpty() && !tailleText.isEmpty()) {
                double poids = Double.parseDouble(poidsText);
                double taille = Double.parseDouble(tailleText);

                if (taille > 0) {
                    double imc = Math.round((poids / (taille * taille)) * 10.0) / 10.0;
                    imcLabel.setText(String.valueOf(imc));

                    // Interprétation de l'IMC
                    if (imc < 18.5) {
                        interpretationLabel.setText("Insuffisance pondérale ⚠️");
                        interpretationLabel.setStyle("-fx-text-fill: #f39c12;");
                    } else if (imc < 25) {
                        interpretationLabel.setText("Poids normal ✅");
                        interpretationLabel.setStyle("-fx-text-fill: #2ecc71;");
                    } else if (imc < 30) {
                        interpretationLabel.setText("Surpoids ⚠️");
                        interpretationLabel.setStyle("-fx-text-fill: #f39c12;");
                    } else if (imc < 35) {
                        interpretationLabel.setText("Obésité modérée 🔴");
                        interpretationLabel.setStyle("-fx-text-fill: #e74c3c;");
                    } else if (imc < 40) {
                        interpretationLabel.setText("Obésité sévère 🔴🔴");
                        interpretationLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    } else {
                        interpretationLabel.setText("Obésité morbide 🔴🔴🔴");
                        interpretationLabel.setStyle("-fx-text-fill: #c0392b; -fx-font-weight: bold;");
                    }
                    return;
                }
            }
            imcLabel.setText("--");
            interpretationLabel.setText("Entrez poids et taille");
            interpretationLabel.setStyle("-fx-text-fill: #7f8c8d;");
        } catch (NumberFormatException e) {
            imcLabel.setText("--");
            interpretationLabel.setText("Valeurs invalides");
            interpretationLabel.setStyle("-fx-text-fill: #e74c3c;");
        }
    }

    // ============ CRUD PARAMÈTRES MÉDICAUX ============

    @FXML
    private void ajouterParametre() {
        try {
            // Vérification des champs
            if (poidsField.getText().isEmpty() || tailleField.getText().isEmpty() ||
                    glycemieField.getText().isEmpty() || tensionField.getText().isEmpty()) {
                showAlert("Erreur", "Tous les champs sont obligatoires", Alert.AlertType.ERROR);
                return;
            }

            ParaMedical paraMedical = new ParaMedical();
            paraMedical.setPoids(Double.parseDouble(poidsField.getText()));
            paraMedical.setTaille(Double.parseDouble(tailleField.getText()));
            paraMedical.setGlycemie(Double.parseDouble(glycemieField.getText()));
            paraMedical.setTensionSystolique(tensionField.getText());
            paraMedical.setCreatedAt(LocalDateTime.now());
            paraMedical.setMedicalRecord(currentMedicalRecord);

            paraMedicalService.ajouter(paraMedical);
            rafraichirTableauParametres();
            viderChamps();
            showAlert("Succès", "Paramètres médicaux ajoutés avec succès", Alert.AlertType.INFORMATION);

        } catch (NumberFormatException e) {
            showAlert("Erreur", "Veuillez entrer des nombres valides", Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'ajout : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void modifierParametre() {
        ParaMedical selected = parametreTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner un paramètre à modifier", Alert.AlertType.WARNING);
            return;
        }

        try {
            selected.setPoids(Double.parseDouble(poidsField.getText()));
            selected.setTaille(Double.parseDouble(tailleField.getText()));
            selected.setGlycemie(Double.parseDouble(glycemieField.getText()));
            selected.setTensionSystolique(tensionField.getText());

            paraMedicalService.modifier(selected);
            rafraichirTableauParametres();
            viderChamps();
            showAlert("Succès", "Paramètres modifiés avec succès", Alert.AlertType.INFORMATION);

        } catch (NumberFormatException e) {
            showAlert("Erreur", "Veuillez entrer des nombres valides", Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la modification : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void supprimerParametre() {
        ParaMedical selected = parametreTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner un paramètre à supprimer", Alert.AlertType.WARNING);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setContentText("Voulez-vous vraiment supprimer ces paramètres ?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            paraMedicalService.supprimer(selected.getId());
            rafraichirTableauParametres();
            viderChamps();
            showAlert("Succès", "Paramètres supprimés avec succès", Alert.AlertType.INFORMATION);
        }
    }

    // ============ RAFRAÎCHISSEMENT ============

    @FXML
    private void rafraichirTableaux() {
        rafraichirTableauParametres();
        rafraichirTableauPrescriptions();
    }

    private void rafraichirTableauParametres() {
        parametresList.clear();
        parametresList.addAll(paraMedicalService.afficherParMedicalRecord(currentMedicalRecord.getId()));
        parametreTable.setItems(parametresList);
    }

    private void rafraichirTableauPrescriptions() {
        prescriptionsList.clear();
        prescriptionsList.addAll(prescriptionService.afficherParMedicalRecord(currentMedicalRecord.getId()));
        prescriptionTable.setItems(prescriptionsList);
    }

    // ============ UTILITAIRES ============

    private void remplirChamps(ParaMedical p) {
        poidsField.setText(String.valueOf(p.getPoids()));
        tailleField.setText(String.valueOf(p.getTaille()));
        glycemieField.setText(String.valueOf(p.getGlycemie()));
        tensionField.setText(p.getTensionSystolique());
        calculerIMC();
    }

    private void viderChamps() {
        poidsField.clear();
        tailleField.clear();
        glycemieField.clear();
        tensionField.clear();
        imcLabel.setText("--");
        interpretationLabel.setText("--");
        interpretationLabel.setStyle("-fx-text-fill: #7f8c8d;");
        parametreTable.getSelectionModel().clearSelection();
    }

    private void showAlert(String titre, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ============ NAVIGATION ============

    @FXML
    private void changerVersMedecin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/medecin_prescriptions.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) parametreTable.getScene().getWindow();
            stage.setScene(new Scene(root, 1100, 700));
            stage.setTitle("VitaHealth - Espace Médecin");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de changer d'interface : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void aPropos() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("À propos");
        alert.setHeaderText("VitaHealth");
        alert.setContentText("Application de gestion des paramètres médicaux\nVersion 1.0\n© 2024 VitaHealth");
        alert.showAndWait();
    }

    @FXML
    private void quitter() {
        Platform.exit();
    }
}