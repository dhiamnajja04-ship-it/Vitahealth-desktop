package org.example.services;

import org.example.entities.Commentaire;
import org.example.utils.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentaireService implements IService<Commentaire> {

    private Connection connection;

    public CommentaireService() {
        connection = MyConnection.getInstance().getConnection();
    }

    @Override
    public void ajouter(Commentaire c) {
        String query = "INSERT INTO commentaire (contenu, auteur, date_creation, sujet_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, c.getContenu());
            ps.setString(2, c.getAuteur());
            ps.setTimestamp(3, c.getDateCreation());
            ps.setInt(4, c.getSujetId());
            ps.executeUpdate();
            System.out.println("✅ Commentaire ajouté par " + c.getAuteur());
        } catch (SQLException e) {
            System.err.println("❌ Erreur ajout commentaire : " + e.getMessage());
        }
    }

    @Override
    public void modifier(Commentaire c) {
        String query = "UPDATE commentaire SET contenu=?, auteur=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, c.getContenu());
            ps.setString(2, c.getAuteur());
            ps.setInt(3, c.getId());
            ps.executeUpdate();
            System.out.println("✅ Commentaire modifié : id=" + c.getId());
        } catch (SQLException e) {
            System.err.println("❌ Erreur modification commentaire : " + e.getMessage());
        }
    }

    @Override
    public void supprimer(int id) {
        String query = "DELETE FROM commentaire WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("✅ Commentaire supprimé : id=" + id);
        } catch (SQLException e) {
            System.err.println("❌ Erreur suppression commentaire : " + e.getMessage());
        }
    }

    @Override
    public Commentaire getById(int id) {
        String query = "SELECT * FROM commentaire WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("❌ Erreur getById commentaire : " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Commentaire> getAll() {
        List<Commentaire> list = new ArrayList<>();
        String query = "SELECT * FROM commentaire ORDER BY date_creation DESC";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("❌ Erreur getAll commentaires : " + e.getMessage());
        }
        return list;
    }

    // ── Fonctionnalités avancées ───────────────────────────────────────────────

    public List<Commentaire> getBySujet(int sujetId) {
        List<Commentaire> list = new ArrayList<>();
        String query = "SELECT * FROM commentaire WHERE sujet_id=? ORDER BY date_creation ASC";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, sujetId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("❌ Erreur getBySujet : " + e.getMessage());
        }
        return list;
    }

    public int countBySujet(int sujetId) {
        String query = "SELECT COUNT(*) FROM commentaire WHERE sujet_id=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, sujetId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("❌ Erreur count : " + e.getMessage());
        }
        return 0;
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private Commentaire mapRow(ResultSet rs) throws SQLException {
        return new Commentaire(
                rs.getInt("id"),
                rs.getString("contenu"),
                rs.getString("auteur"),
                rs.getTimestamp("date_creation"),
                rs.getInt("sujet_id")
        );
    }
}