package com.vitahealth.controllers;

import com.vitahealth.database.DatabaseConnection;
import com.vitahealth.entity.MedicalRecord;
import com.vitahealth.entity.ParaMedical;
import com.vitahealth.entity.Prescription;
import com.vitahealth.services.ParaMedicalService;
import com.vitahealth.services.PrescriptionService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
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
import java.util.HashMap;
import java.util.Map;

public class MedecinController {

    // Champs pour prescriptions
    @FXML private ComboBox<String> patientCombo;
    @FXML private TextArea medicamentsArea;
    @FXML private TextField dureeField;
    @FXML private TextArea instructionsArea;
    @FXML private TableView<Prescription> prescriptionTable;
    @FXML private TableColumn<Prescription, LocalDateTime> colDate;
    @FXML private TableColumn<Prescription, String> colMedicaments;
    @FXML private TableColumn<Prescription, String> colDuree;
    @FXML private TableColumn<Prescription, String> colInstructions;

    // Champs pour paramètres médicaux
    @FXML private TableView<ParaMedical> parametreTable;
    @FXML private TableColumn<ParaMedical, LocalDateTime> colParamDate;
    @FXML private TableColumn<ParaMedical, Double> colPoids;
    @FXML private TableColumn<ParaMedical, Double> colTaille;
    @FXML private TableColumn<ParaMedical, Double> colGlycemie;
    @FXML private TableColumn<ParaMedical, String> colTension;
    @FXML private TableColumn<ParaMedical, Double> colImc;
    @FXML private TableColumn<ParaMedical, String> colInterpretation;

    // Filtre
    @FXML private ComboBox<String> filtreMaladieCombo;

    private PrescriptionService prescriptionService;
    private ParaMedicalService paraMedicalService;
    private ObservableList<Prescription> prescriptionsList;
    private ObservableList<ParaMedical> parametresList;
    private Map<String, Integer> patientMap; // nom -> medical_record_id
    private Integer currentMedicalRecordId;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private String currentFiltreMaladie = "";

    @FXML
    public void initialize() {
        prescriptionService = new PrescriptionService();
        paraMedicalService = new ParaMedicalService();
        prescriptionsList = FXCollections.observableArrayList();
        parametresList = FXCollections.observableArrayList();
        patientMap = new HashMap<>();

        // Configuration colonnes prescriptions
        colDate.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        colMedicaments.setCellValueFactory(new PropertyValueFactory<>("medicationList"));
        colDuree.setCellValueFactory(new PropertyValueFactory<>("duration"));
        colInstructions.setCellValueFactory(new PropertyValueFactory<>("instructions"));
        colDate.setCellFactory(column -> new TableCell<Prescription, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(item.format(formatter));
            }
        });

        // Configuration colonnes paramètres médicaux
        colParamDate.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        colPoids.setCellValueFactory(new PropertyValueFactory<>("poids"));
        colTaille.setCellValueFactory(new PropertyValueFactory<>("taille"));
        colGlycemie.setCellValueFactory(new PropertyValueFactory<>("glycemie"));
        colTension.setCellValueFactory(new PropertyValueFactory<>("tensionSystolique"));
        colImc.setCellValueFactory(new PropertyValueFactory<>("imc"));
        colInterpretation.setCellValueFactory(cellData -> {
            ParaMedical p = cellData.getValue();
            String interp = (p.getImc() != null) ? p.getImcInterpretation() : "Non calculable";
            return new SimpleStringProperty(interp);
        });
        colParamDate.setCellFactory(column -> new TableCell<ParaMedical, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(item.format(formatter));
            }
        });

        // Remplir la liste des maladies disponibles dans le filtre
        chargerMaladies();

        // Charger la liste des patients sans filtre initial
        chargerListePatients();

        // Listener pour la sélection du patient
        prescriptionTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, newVal) -> {
                    if (newVal != null) remplirChamps(newVal);
                });
    }

    private void chargerMaladies() {
        ObservableList<String> maladies = FXCollections.observableArrayList();
        maladies.add("Toutes les maladies");
        String sql = "SELECT DISTINCT maladie FROM user WHERE maladie IS NOT NULL AND maladie != ''";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String m = rs.getString("maladie");
                if (m != null && !m.isEmpty()) maladies.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        filtreMaladieCombo.setItems(maladies);
        filtreMaladieCombo.getSelectionModel().selectFirst(); // "Toutes les maladies"
    }

    private void chargerListePatients() {
        patientMap.clear();
        String sql;
        if (currentFiltreMaladie.isEmpty() || currentFiltreMaladie.equals("Toutes les maladies")) {
            sql = "SELECT u.id, u.first_name, u.last_name, u.maladie, mr.id as medical_record_id " +
                    "FROM user u " +
                    "LEFT JOIN medical_record mr ON u.id = mr.patient_id " +
                    "WHERE u.roles LIKE '%PATIENT%'";
        } else {
            sql = "SELECT u.id, u.first_name, u.last_name, u.maladie, mr.id as medical_record_id " +
                    "FROM user u " +
                    "LEFT JOIN medical_record mr ON u.id = mr.patient_id " +
                    "WHERE u.roles LIKE '%PATIENT%' AND u.maladie = ?";
        }
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (!currentFiltreMaladie.isEmpty() && !currentFiltreMaladie.equals("Toutes les maladies")) {
                ps.setString(1, currentFiltreMaladie);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int userId = rs.getInt("id");
                    int medicalRecordId = rs.getInt("medical_record_id");
                    if (medicalRecordId == 0) {
                        medicalRecordId = creerMedicalRecord(userId);
                    }
                    String nom = rs.getString("first_name") + " " + rs.getString("last_name");
                    if (rs.getString("maladie") != null && !rs.getString("maladie").isEmpty()) {
                        nom += " (" + rs.getString("maladie") + ")";
                    }
                    nom += " (ID: " + userId + ")";
                    patientMap.put(nom, medicalRecordId);
                }
            }
            patientCombo.setItems(FXCollections.observableArrayList(patientMap.keySet()));
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la liste des patients", Alert.AlertType.ERROR);
        }
    }

    private int creerMedicalRecord(int userId) {
        String sql = "INSERT INTO medical_record (patient_id, created_at) VALUES (?, NOW())";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @FXML
    private void filtrerPatients() {
        String selected = filtreMaladieCombo.getSelectionModel().getSelectedItem();
        if (selected == null || selected.equals("Toutes les maladies")) {
            currentFiltreMaladie = "";
        } else {
            currentFiltreMaladie = selected;
        }
        chargerListePatients();
        patientCombo.getSelectionModel().clearSelection();
        prescriptionTable.setItems(FXCollections.observableArrayList());
        parametreTable.setItems(FXCollections.observableArrayList());
        currentMedicalRecordId = null;
    }

    @FXML
    private void reinitialiserFiltre() {
        filtreMaladieCombo.getSelectionModel().selectFirst();
        currentFiltreMaladie = "";
        chargerListePatients();
        patientCombo.getSelectionModel().clearSelection();
        prescriptionTable.setItems(FXCollections.observableArrayList());
        parametreTable.setItems(FXCollections.observableArrayList());
        currentMedicalRecordId = null;
    }

    @FXML
    private void rafraichirListePatients() {
        chargerListePatients();
        patientCombo.getSelectionModel().clearSelection();
        prescriptionTable.setItems(FXCollections.observableArrayList());
        parametreTable.setItems(FXCollections.observableArrayList());
        currentMedicalRecordId = null;
    }

    @FXML
    private void chargerDonneesPatient() {
        String selected = patientCombo.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner un patient", Alert.AlertType.WARNING);
            return;
        }
        currentMedicalRecordId = patientMap.get(selected);
        if (currentMedicalRecordId == null) {
            showAlert("Erreur", "Aucun dossier médical trouvé pour ce patient", Alert.AlertType.ERROR);
            return;
        }
        // Charger prescriptions
        prescriptionsList.clear();
        prescriptionsList.addAll(prescriptionService.afficherParMedicalRecord(currentMedicalRecordId));
        prescriptionTable.setItems(prescriptionsList);
        // Charger paramètres médicaux
        parametresList.clear();
        parametresList.addAll(paraMedicalService.afficherParMedicalRecord(currentMedicalRecordId));
        parametreTable.setItems(parametresList);
        viderChamps();
    }

    // --- Méthodes CRUD pour prescriptions (inchangées) ---
    @FXML
    private void ajouterPrescription() {
        if (currentMedicalRecordId == null) {
            showAlert("Erreur", "Sélectionnez un patient d'abord", Alert.AlertType.WARNING);
            return;
        }
        if (medicamentsArea.getText().isEmpty() || dureeField.getText().isEmpty()) {
            showAlert("Erreur", "Les médicaments et la durée sont obligatoires", Alert.AlertType.ERROR);
            return;
        }
        try {
            Prescription p = new Prescription();
            p.setMedicationList(medicamentsArea.getText());
            p.setDuration(dureeField.getText());
            p.setInstructions(instructionsArea.getText());
            p.setCreatedAt(LocalDateTime.now());

            MedicalRecord mr = new MedicalRecord();
            mr.setId(currentMedicalRecordId);
            p.setMedicalRecord(mr);

            prescriptionService.ajouter(p);
            chargerDonneesPatient();
            viderChamps();
            showAlert("Succès", "Prescription ajoutée", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            showAlert("Erreur", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void modifierPrescription() {
        Prescription selected = prescriptionTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Sélectionnez une prescription à modifier", Alert.AlertType.WARNING);
            return;
        }
        try {
            selected.setMedicationList(medicamentsArea.getText());
            selected.setDuration(dureeField.getText());
            selected.setInstructions(instructionsArea.getText());
            prescriptionService.modifier(selected);
            chargerDonneesPatient();
            viderChamps();
            showAlert("Succès", "Prescription modifiée", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            showAlert("Erreur", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void supprimerPrescription() {
        Prescription selected = prescriptionTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Sélectionnez une prescription à supprimer", Alert.AlertType.WARNING);
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setContentText("Voulez-vous vraiment supprimer cette prescription ?");
        if (confirm.showAndWait().get() == ButtonType.OK) {
            prescriptionService.supprimer(selected.getId());
            chargerDonneesPatient();
            viderChamps();
            showAlert("Succès", "Prescription supprimée", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void viderChamps() {
        medicamentsArea.clear();
        dureeField.clear();
        instructionsArea.clear();
        prescriptionTable.getSelectionModel().clearSelection();
    }

    private void remplirChamps(Prescription p) {
        medicamentsArea.setText(p.getMedicationList());
        dureeField.setText(p.getDuration());
        instructionsArea.setText(p.getInstructions());
    }

    // --- Navigation ---
    @FXML
    private void deconnecter() {
        try {
            Stage stage = (Stage) patientCombo.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            stage.setScene(new Scene(root, 600, 400));
            stage.setTitle("VitaHealth - Connexion");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void quitter() {
        Platform.exit();
    }

    @FXML
    private void aPropos() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("À propos");
        alert.setHeaderText("VitaHealth - Espace Médecin");
        alert.setContentText("Gestion des prescriptions et consultation des paramètres médicaux des patients.\nVersion 1.0");
        alert.showAndWait();
    }

    private void showAlert(String titre, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}