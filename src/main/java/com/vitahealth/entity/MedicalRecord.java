package com.vitahealth.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MedicalRecord {
    private int id;
    private User patient;
    private Appointment appointment;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String diagnosis;
    private String allergies;
    private String medicalHistory;
    private String currentTreatments;
    private String bloodType;
    private List<Prescription> prescriptions = new ArrayList<>();
    private List<ParaMedical> parameters = new ArrayList<>();

    public MedicalRecord() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public User getPatient() { return patient; }
    public void setPatient(User patient) { this.patient = patient; }

    public Appointment getAppointment() { return appointment; }
    public void setAppointment(Appointment appointment) { this.appointment = appointment; }

    public String getContent() { return content; }
    public void setContent(String content) {
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
        this.updatedAt = LocalDateTime.now();
    }

    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) {
        this.allergies = allergies;
        this.updatedAt = LocalDateTime.now();
    }

    public String getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
        this.updatedAt = LocalDateTime.now();
    }

    public String getCurrentTreatments() { return currentTreatments; }
    public void setCurrentTreatments(String currentTreatments) {
        this.currentTreatments = currentTreatments;
        this.updatedAt = LocalDateTime.now();
    }

    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
        this.updatedAt = LocalDateTime.now();
    }

    public List<Prescription> getPrescriptions() { return prescriptions; }
    public List<ParaMedical> getParameters() { return parameters; }

    public void addPrescription(Prescription prescription) {
        if (!this.prescriptions.contains(prescription)) {
            this.prescriptions.add(prescription);
            prescription.setMedicalRecord(this);
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void removePrescription(Prescription prescription) {
        if (this.prescriptions.remove(prescription)) {
            if (prescription.getMedicalRecord() == this) {
                prescription.setMedicalRecord(null);
            }
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void addParameter(ParaMedical parameter) {
        if (!this.parameters.contains(parameter)) {
            this.parameters.add(parameter);
            parameter.setMedicalRecord(this);
        }
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isComplete() {
        return (content != null && !content.isEmpty()) ||
                (diagnosis != null && !diagnosis.isEmpty()) ||
                !prescriptions.isEmpty() ||
                !parameters.isEmpty();
    }

    public int getTotalItems() {
        return prescriptions.size() + parameters.size();
    }
}
