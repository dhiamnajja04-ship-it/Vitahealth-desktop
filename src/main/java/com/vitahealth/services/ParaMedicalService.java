package com.vitahealth.services;

import com.vitahealth.database.DatabaseConnection;
import com.vitahealth.entity.MedicalRecord;
import com.vitahealth.entity.ParaMedical;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ParaMedicalService {

    // CREATE
    public void ajouter(ParaMedical paraMedical) {
        String sql = "INSERT INTO paramedical (poids, taille, glycemie, tension_systolique, created_at, medical_record_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDouble(1, paraMedical.getPoids());
            ps.setDouble(2, paraMedical.getTaille());
            ps.setDouble(3, paraMedical.getGlycemie());
            ps.setString(4, paraMedical.getTensionSystolique());
            ps.setTimestamp(5, Timestamp.valueOf(paraMedical.getCreatedAt()));
            ps.setInt(6, paraMedical.getMedicalRecord().getId());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    paraMedical.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // READ all for a given medical_record
    public List<ParaMedical> afficherParMedicalRecord(int medicalRecordId) {
        List<ParaMedical> list = new ArrayList<>();
        String sql = "SELECT * FROM paramedical WHERE medical_record_id = ? ORDER BY created_at DESC";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, medicalRecordId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ParaMedical p = new ParaMedical();
                    p.setId(rs.getInt("id"));
                    p.setPoids(rs.getDouble("poids"));
                    p.setTaille(rs.getDouble("taille"));
                    p.setGlycemie(rs.getDouble("glycemie"));
                    p.setTensionSystolique(rs.getString("tension_systolique"));
                    p.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

                    MedicalRecord mr = new MedicalRecord();
                    mr.setId(rs.getInt("medical_record_id"));
                    p.setMedicalRecord(mr);

                    list.add(p);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // READ one by id
    public ParaMedical getById(int id) {
        String sql = "SELECT * FROM paramedical WHERE id = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ParaMedical p = new ParaMedical();
                    p.setId(rs.getInt("id"));
                    p.setPoids(rs.getDouble("poids"));
                    p.setTaille(rs.getDouble("taille"));
                    p.setGlycemie(rs.getDouble("glycemie"));
                    p.setTensionSystolique(rs.getString("tension_systolique"));
                    p.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

                    MedicalRecord mr = new MedicalRecord();
                    mr.setId(rs.getInt("medical_record_id"));
                    p.setMedicalRecord(mr);
                    return p;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // UPDATE
    public void modifier(ParaMedical paraMedical) {
        String sql = "UPDATE paramedical SET poids=?, taille=?, glycemie=?, tension_systolique=? WHERE id=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setDouble(1, paraMedical.getPoids());
            ps.setDouble(2, paraMedical.getTaille());
            ps.setDouble(3, paraMedical.getGlycemie());
            ps.setString(4, paraMedical.getTensionSystolique());
            ps.setInt(5, paraMedical.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // DELETE
    public void supprimer(int id) {
        String sql = "DELETE FROM paramedical WHERE id=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}