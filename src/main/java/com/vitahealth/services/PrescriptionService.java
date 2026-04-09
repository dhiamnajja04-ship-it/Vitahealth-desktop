package com.vitahealth.services;

import com.vitahealth.entity.Prescription;
import com.vitahealth.entity.MedicalRecord;
import com.vitahealth.database.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionService {

    private Connection connection;

    public PrescriptionService() {
        connection = DatabaseConnection.getConnection();
    }

    // CREATE - Médecin uniquement
    public void ajouter(Prescription prescription) {
        String sql = "INSERT INTO prescription (created_at, medication_list, instructions, duration, medical_record_id) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setTimestamp(1, Timestamp.valueOf(prescription.getCreatedAt()));
            ps.setString(2, prescription.getMedicationList());
            ps.setString(3, prescription.getInstructions());
            ps.setString(4, prescription.getDuration());
            ps.setInt(5, prescription.getMedicalRecord().getId());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                prescription.setId(rs.getInt(1));
            }
            System.out.println("Prescription ajoutée");
        } catch (SQLException e) {
            System.err.println("Erreur ajout : " + e.getMessage());
        }
    }

    // READ - Toutes les prescriptions d'un patient (Patient + Médecin)
    public List<Prescription> afficherParMedicalRecord(int medicalRecordId) {
        List<Prescription> list = new ArrayList<>();
        String sql = "SELECT * FROM prescription WHERE medical_record_id = ? ORDER BY created_at DESC";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, medicalRecordId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Prescription p = new Prescription();
                p.setId(rs.getInt("id"));
                p.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                p.setMedicationList(rs.getString("medication_list"));
                p.setInstructions(rs.getString("instructions"));
                p.setDuration(rs.getString("duration"));

                MedicalRecord mr = new MedicalRecord();
                mr.setId(rs.getInt("medical_record_id"));
                p.setMedicalRecord(mr);

                list.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Erreur affichage : " + e.getMessage());
        }
        return list;
    }

    // READ - Une seule prescription par ID
    public Prescription getById(int id) {
        String sql = "SELECT * FROM prescription WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Prescription p = new Prescription();
                p.setId(rs.getInt("id"));
                p.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                p.setMedicationList(rs.getString("medication_list"));
                p.setInstructions(rs.getString("instructions"));
                p.setDuration(rs.getString("duration"));

                MedicalRecord mr = new MedicalRecord();
                mr.setId(rs.getInt("medical_record_id"));
                p.setMedicalRecord(mr);

                return p;
            }
        } catch (SQLException e) {
            System.err.println("Erreur getById : " + e.getMessage());
        }
        return null;
    }

    // UPDATE - Médecin uniquement
    public void modifier(Prescription prescription) {
        String sql = "UPDATE prescription SET medication_list = ?, instructions = ?, duration = ? WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, prescription.getMedicationList());
            ps.setString(2, prescription.getInstructions());
            ps.setString(3, prescription.getDuration());
            ps.setInt(4, prescription.getId());
            ps.executeUpdate();
            System.out.println("Prescription modifiée");
        } catch (SQLException e) {
            System.err.println("Erreur modification : " + e.getMessage());
        }
    }

    // DELETE - Médecin uniquement
    public void supprimer(int id) {
        String sql = "DELETE FROM prescription WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Prescription supprimée");
        } catch (SQLException e) {
            System.err.println("Erreur suppression : " + e.getMessage());
        }
    }
}