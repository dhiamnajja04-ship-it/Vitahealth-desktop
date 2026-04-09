package com.vitahealth.services;

import com.vitahealth.database.DatabaseConnection;
import com.vitahealth.entity.MedicalRecord;
import com.vitahealth.entity.ParaMedical;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ParaMedicalService {

    private Connection connection;

    public ParaMedicalService() {
        connection = DatabaseConnection.getConnection();
    }

    public void ajouter(ParaMedical p) {
        String sql = "INSERT INTO paramedical (poids, taille, glycemie, tension_systolique, created_at, medical_record_id) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setDouble(1, p.getPoids());
            ps.setDouble(2, p.getTaille());
            ps.setDouble(3, p.getGlycemie());
            ps.setString(4, p.getTensionSystolique());
            ps.setTimestamp(5, Timestamp.valueOf(p.getCreatedAt()));
            ps.setInt(6, p.getMedicalRecord().getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<ParaMedical> afficherParMedicalRecord(int medicalRecordId) {
        List<ParaMedical> list = new ArrayList<>();
        String sql = "SELECT * FROM paramedical WHERE medical_record_id = ? ORDER BY created_at DESC";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, medicalRecordId);
            ResultSet rs = ps.executeQuery();
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void modifier(ParaMedical p) {
        String sql = "UPDATE paramedical SET poids=?, taille=?, glycemie=?, tension_systolique=? WHERE id=?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setDouble(1, p.getPoids());
            ps.setDouble(2, p.getTaille());
            ps.setDouble(3, p.getGlycemie());
            ps.setString(4, p.getTensionSystolique());
            ps.setInt(5, p.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void supprimer(int id) {
        String sql = "DELETE FROM paramedical WHERE id=?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}