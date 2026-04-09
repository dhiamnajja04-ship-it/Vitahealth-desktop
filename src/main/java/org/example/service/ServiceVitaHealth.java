package org.example.service;

import org.example.entity.User;
import org.example.entity.Appointment;
import org.example.DAO.UserDAO;
import org.example.DAO.AppointmentDAO;
import java.sql.SQLException;
import java.util.List;

public class ServiceVitaHealth {
    private UserDAO userDAO;
    private AppointmentDAO appointmentDAO;

    public ServiceVitaHealth() {
        this.userDAO = new UserDAO();
        this.appointmentDAO = new AppointmentDAO();
    }

    // ========== User CRUD ==========
    public void inscrirePatient(User patient) throws SQLException {
        if (userDAO.findByEmail(patient.getEmail()) != null) {
            throw new SQLException("Email déjà utilisé");
        }
        patient.setRole("patient");
        userDAO.ajouter(patient);
    }

    public void inscrireMedecin(User medecin) throws SQLException {
        if (userDAO.findByEmail(medecin.getEmail()) != null) {
            throw new SQLException("Email déjà utilisé");
        }
        medecin.setRole("medecin");
        medecin.setVerified(false);
        userDAO.ajouter(medecin);
    }

    public User login(String email, String password) throws SQLException {
        User user = userDAO.findByEmail(email);
        if (user != null && password.equals(user.getPassword())) {
            return user;
        }
        return null;
    }

    // READ
    public User getUserById(int id) throws SQLException {
        return userDAO.findById(id);
    }

    public List<User> getAllUsers() throws SQLException {
        return userDAO.findAll();
    }

    public List<User> getUsersByRole(String role) throws SQLException {
        return userDAO.findByRole(role);
    }

    public List<User> getAllMedecins() throws SQLException {
        return userDAO.getAllMedecins();
    }

    // UPDATE
    public void updateUser(User user) throws SQLException {
        userDAO.update(user);
    }

    public void updatePatientHealth(int patientId, Double poids, Double taille, Double glycemie, String tension) throws SQLException {
        userDAO.updateHealthParameters(patientId, poids, taille, glycemie, tension);
    }

    // DELETE
    public void deleteUser(int id) throws SQLException {
        userDAO.delete(id);
    }

    // ========== Appointment ==========
    public void prendreRendezVous(Appointment rdv) throws SQLException {
        appointmentDAO.ajouter(rdv);
    }

    // ========== IA Recommandation ==========
    public String getRecommendationIA(User patient) {
        if (patient.getPoids() != null && patient.getTaille() != null) {
            double imc = patient.getPoids() / (patient.getTaille() * patient.getTaille());
            if (imc > 25) return "IMC élevé, pensez à l'exercice.";
            if (imc < 18.5) return "IMC bas, surveillez votre alimentation.";
        }
        if (patient.getGlycemie() != null && patient.getGlycemie() > 1.26) {
            return "Glycémie élevée, consultez votre médecin.";
        }
        return "Tous vos paramètres sont bons !";
    }
}