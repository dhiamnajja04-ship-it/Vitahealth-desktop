package com.vitahealth.controllers;
import com.vitahealth.database.DatabaseConnection;

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

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class PatientController {

    @FXML private TextField poidsField;
    @FXML private TextField tailleField;
    @FXML private TextField glycemieField;
    @FXML private TextField tensionField;
    @FXML private Label imcLabel;
    @FXML private Label interpretationLabel;
    @FXML private Label patientNameLabel; // Ajoutez ce label dans votre FXML

    @FXML private TableView<ParaMedical> parametreTable;
    @FXML private TableColumn<ParaMedical, LocalDateTime> colDate;
    @FXML private TableColumn<ParaMedical, Double> colPoids, colTaille, colGlycemie, colImc;
    @FXML private TableColumn<ParaMedical, String> colTension;

    @FXML private TableView<Prescription> prescriptionTable;
    @FXML private TableColumn<Prescription, LocalDateTime> colPrescDate;
    @FXML private TableColumn<Prescription, String> colMedicaments, colDuree, colInstructions;

    private ParaMedicalService paraMedicalService;
    private PrescriptionService prescriptionService;

    private int currentUserId;
    private String currentUserEmail;
    private int currentMedicalRecordId;

    private ObservableList<ParaMedical> parametresList;
    private ObservableList<Prescription> prescriptionsList;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        paraMedicalService = new ParaMedicalService();
        prescriptionService = new PrescriptionService();

        parametresList = FXCollections.observableArrayList();
        prescriptionsList = FXCollections.observableArrayList();

        // Configuration des colonnes
        colDate.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        colPoids.setCellValueFactory(new PropertyValueFactory<>("poids"));
        colTaille.setCellValueFactory(new PropertyValueFactory<>("taille"));
        colGlycemie.setCellValueFactory(new PropertyValueFactory<>("glycemie"));
        colTension.setCellValueFactory(new PropertyValueFactory<>("tensionSystolique"));
        colImc.setCellValueFactory(new PropertyValueFactory<>("imc"));

        colDate.setCellFactory(column -> new TableCell<ParaMedical, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(item.format(formatter));
            }
        });

        colPrescDate.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        colMedicaments.setCellValueFactory(new PropertyValueFactory<>("medicationList"));
        colDuree.setCellValueFactory(new PropertyValueFactory<>("duration"));
        colInstructions.setCellValueFactory(new PropertyValueFactory<>("instructions"));

        colPrescDate.setCellFactory(column -> new TableCell<Prescription, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(item.format(formatter));
            }
        });

        // Calcul IMC en temps réel
        poidsField.textProperty().addListener((obs, old, newVal) -> calculerIMC());
        tailleField.textProperty().addListener((obs, old, newVal) -> calculerIMC());

        // Sélection dans le tableau
        parametreTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, newVal) -> {
                    if (newVal != null) remplirChamps(newVal);
                }
        );
    }

    // Appelé depuis MainApp après l'authentification
    public void setCurrentUser(int userId, String email) {
        this.currentUserId = userId;
        this.currentUserEmail = email;

        // Récupérer le medical_record_id associé à cet utilisateur
        chargerMedicalRecordId();

        // Afficher le nom du patient
        if (patientNameLabel != null) {
            patientNameLabel.setText("Patient: " + email);
        }

        // Charger les données
        rafraichirTableaux();
    }

    private void chargerMedicalRecordId() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // S'assurer que la table existe avec patient_id
            String createTable = "CREATE TABLE IF NOT EXISTS medical_record (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "patient_id INT NOT NULL, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (patient_id) REFERENCES user(id) ON DELETE CASCADE)";
            try (Statement st = conn.createStatement()) {
                st.execute(createTable);
            }

            // Requête avec patient_id
            String selectSql = "SELECT id FROM medical_record WHERE patient_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
                ps.setInt(1, currentUserId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        currentMedicalRecordId = rs.getInt("id");
                    } else {
                        // Insertion avec patient_id
                        String insertSql = "INSERT INTO medical_record (patient_id) VALUES (?)";
                        try (PreparedStatement insertPs = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                            insertPs.setInt(1, currentUserId);
                            insertPs.executeUpdate();
                            try (ResultSet generatedKeys = insertPs.getGeneratedKeys()) {
                                if (generatedKeys.next()) {
                                    currentMedicalRecordId = generatedKeys.getInt(1);
                                }
                            }
                        }
                    }
                }
            }
            System.out.println("MedicalRecord ID: " + currentMedicalRecordId);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur DB", "Erreur medical_record : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void calculerIMC() {
        try {
            double poids = Double.parseDouble(poidsField.getText());
            double taille = Double.parseDouble(tailleField.getText());
            if (taille > 0) {
                double imc = Math.round((poids / (taille * taille)) * 10.0) / 10.0;
                imcLabel.setText(String.valueOf(imc));

                if (imc < 18.5) interpretationLabel.setText("Insuffisance pondérale");
                else if (imc < 25) interpretationLabel.setText("Poids normal");
                else if (imc < 30) interpretationLabel.setText("Surpoids");
                else if (imc < 35) interpretationLabel.setText("Obésité modérée");
                else if (imc < 40) interpretationLabel.setText("Obésité sévère");
                else interpretationLabel.setText("Obésité morbide");
            }
        } catch (NumberFormatException e) {
            imcLabel.setText("--");
            interpretationLabel.setText("--");
        }
    }

    @FXML
    private void ajouterParametre() {
        try {
            ParaMedical p = new ParaMedical();
            p.setPoids(Double.parseDouble(poidsField.getText()));
            p.setTaille(Double.parseDouble(tailleField.getText()));
            p.setGlycemie(Double.parseDouble(glycemieField.getText()));
            p.setTensionSystolique(tensionField.getText());
            p.setCreatedAt(LocalDateTime.now());

            MedicalRecord mr = new MedicalRecord();
            mr.setId(currentMedicalRecordId);
            p.setMedicalRecord(mr);

            paraMedicalService.ajouter(p);
            rafraichirTableauParametres();
            viderChamps();
            showAlert("Succès", "Paramètres ajoutés", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            showAlert("Erreur", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void modifierParametre() {
        ParaMedical selected = parametreTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Sélectionnez un paramètre", Alert.AlertType.WARNING);
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
            showAlert("Succès", "Paramètres modifiés", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            showAlert("Erreur", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void supprimerParametre() {
        ParaMedical selected = parametreTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Sélectionnez un paramètre", Alert.AlertType.WARNING);
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setContentText("Voulez-vous vraiment supprimer ?");
        if (confirm.showAndWait().get() == ButtonType.OK) {
            paraMedicalService.supprimer(selected.getId());
            rafraichirTableauParametres();
            viderChamps();
            showAlert("Succès", "Paramètres supprimés", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void rafraichirTableaux() {
        rafraichirTableauParametres();
        rafraichirTableauPrescriptions();
    }

    private void rafraichirTableauParametres() {
        parametresList.clear();
        parametresList.addAll(paraMedicalService.afficherParMedicalRecord(currentMedicalRecordId));
        parametreTable.setItems(parametresList);
    }

    private void rafraichirTableauPrescriptions() {
        prescriptionsList.clear();
        prescriptionsList.addAll(prescriptionService.afficherParMedicalRecord(currentMedicalRecordId));
        prescriptionTable.setItems(prescriptionsList);
    }

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
        parametreTable.getSelectionModel().clearSelection();
    }

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
            showAlert("Erreur", "Impossible de changer d'interface", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void aPropos() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("À propos");
        alert.setContentText("VitaHealth - Gestion des paramètres médicaux");
        alert.showAndWait();
    }

    @FXML
    private void quitter() {
        Platform.exit();
    }

    private void showAlert(String titre, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}