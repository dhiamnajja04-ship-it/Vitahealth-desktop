package com.vitahealth.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class User {
    private int id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String cinPhoto;
    private String diplome;
    private String specialite;
    private boolean isVerified;
    private String maladie;
    private double poids;
    private double taille;
    private double glycemie;
    private String tension;
    private LocalDateTime lastParameterAt;
    private String imageName;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;

    private List<String> roles = new ArrayList<>();
    private List<Appointment> appointmentsAsPatient = new ArrayList<>();
    private List<Appointment> appointmentsAsDoctor = new ArrayList<>();
    private MedicalRecord medicalRecord;
    private List<Commentaire> commentaires = new ArrayList<>();
    private List<Forum> forums = new ArrayList<>();
    private List<Reclamation> reclamations = new ArrayList<>();
    private List<Notification> notifications = new ArrayList<>();

    public User() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.lastParameterAt = LocalDateTime.now();
        this.roles.add("ROLE_USER");
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFullName() { return firstName + " " + lastName; }

    public String getCinPhoto() { return cinPhoto; }
    public void setCinPhoto(String cinPhoto) { this.cinPhoto = cinPhoto; }

    public String getDiplome() { return diplome; }
    public void setDiplome(String diplome) { this.diplome = diplome; }

    public String getSpecialite() { return specialite; }
    public void setSpecialite(String specialite) { this.specialite = specialite; }

    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { this.isVerified = verified; }

    public String getMaladie() { return maladie; }
    public void setMaladie(String maladie) { this.maladie = maladie; }

    public double getPoids() { return poids; }
    public void setPoids(double poids) { this.poids = poids; }

    public double getTaille() { return taille; }
    public void setTaille(double taille) { this.taille = taille; }

    public double getGlycemie() { return glycemie; }
    public void setGlycemie(double glycemie) { this.glycemie = glycemie; }

    public String getTension() { return tension; }
    public void setTension(String tension) { this.tension = tension; }

    public LocalDateTime getLastParameterAt() { return lastParameterAt; }
    public void setLastParameterAt(LocalDateTime date) { this.lastParameterAt = date; }

    public String getImageName() { return imageName; }
    public void setImageName(String imageName) { this.imageName = imageName; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }

    public MedicalRecord getMedicalRecord() { return medicalRecord; }
    public void setMedicalRecord(MedicalRecord medicalRecord) {
        this.medicalRecord = medicalRecord;
        if (medicalRecord != null && medicalRecord.getPatient() != this) {
            medicalRecord.setPatient(this);
        }
    }

    public List<Appointment> getAppointmentsAsPatient() { return appointmentsAsPatient; }
    public List<Appointment> getAppointmentsAsDoctor() { return appointmentsAsDoctor; }
    public List<Commentaire> getCommentaires() { return commentaires; }
    public List<Forum> getForums() { return forums; }
    public List<Reclamation> getReclamations() { return reclamations; }
    public List<Notification> getNotifications() { return notifications; }

    public Double getImc() {
        if (poids > 0 && taille > 0) {
            return Math.round((poids / (taille * taille)) * 10.0) / 10.0;
        }
        return null;
    }

    @Override
    public String toString() {
        return getFullName();
    }
}