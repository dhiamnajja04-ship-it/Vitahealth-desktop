package com.vitahealth.controllers;

import com.vitahealth.entity.Prescription;
import com.vitahealth.entity.MedicalRecord;
import com.vitahealth.services.PrescriptionService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class MedecinController {

    @FXML private ComboBox<String> patientCombo;
    @FXML private TextArea medicamentsArea;
    @FXML private TextField dureeField;
    @FXML private TextArea instructionsArea;

    @FXML private TableView<Prescription> prescriptionTable;
    @FXML private TableColumn<Prescription, LocalDateTime> colDate;
    @FXML private TableColumn<Prescription, String> colMedicaments, colDuree, colInstructions;

    private PrescriptionService prescriptionService;
    private ObservableList<Prescription> prescriptionsList;
    private Map<String, Integer> patientMap; // nom -> medicalRecordId
    private Integer currentMedicalRecordId;

    @FXML
    public void initialize() {
        prescriptionService = new PrescriptionService();
        prescriptionsList = FXCollections.observableArrayList();
        patientMap = new HashMap<>();

        colDate.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        colMedicaments.setCellValueFactory(new PropertyValueFactory<>("medicationList"));
        colDuree.setCellValueFactory(new PropertyValueFactory<>("duration"));
        colInstructions.setCellValueFactory(new PropertyValueFactory<>("instructions"));

        chargerPatients();

        prescriptionTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, newVal) -> {
                    if (newVal != null) remplirChamps(newVal);
                }
        );
    }

    private void chargerPatients() {
        // À remplacer par votre vrai service Patient
        patientMap.put("Jean Dupont", 1);
        patientMap.put("Marie Martin", 2);
        patientMap.put("Pierre Durand", 3);

        patientCombo.setItems(FXCollections.observableArrayList(patientMap.keySet()));
    }

    @FXML
    private void chargerPrescriptions() {
        String selected = patientCombo.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Sélectionnez un patient", Alert.AlertType.WARNING);
            return;
        }

        currentMedicalRecordId = patientMap.get(selected);
        prescriptionsList.clear();
        prescriptionsList.addAll(prescriptionService.afficherParMedicalRecord(currentMedicalRecordId));
        prescriptionTable.setItems(prescriptionsList);
        viderChamps();
    }

    @FXML
    private void ajouterPrescription() {
        if (currentMedicalRecordId == null) {
            showAlert("Erreur", "Sélectionnez un patient d'abord", Alert.AlertType.WARNING);
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
            chargerPrescriptions();
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
            showAlert("Erreur", "Sélectionnez une prescription", Alert.AlertType.WARNING);
            return;
        }

        try {
            selected.setMedicationList(medicamentsArea.getText());
            selected.setDuration(dureeField.getText());
            selected.setInstructions(instructionsArea.getText());

            prescriptionService.modifier(selected);
            chargerPrescriptions();
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
            showAlert("Erreur", "Sélectionnez une prescription", Alert.AlertType.WARNING);
            return;
        }

        prescriptionService.supprimer(selected.getId());
        chargerPrescriptions();
        viderChamps();
        showAlert("Succès", "Prescription supprimée", Alert.AlertType.INFORMATION);
    }

    private void remplirChamps(Prescription p) {
        medicamentsArea.setText(p.getMedicationList());
        dureeField.setText(p.getDuration());
        instructionsArea.setText(p.getInstructions());
    }

    private void viderChamps() {
        medicamentsArea.clear();
        dureeField.clear();
        instructionsArea.clear();
    }

    private void showAlert(String titre, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setContentText(message);
        alert.showAndWait();
    }
}