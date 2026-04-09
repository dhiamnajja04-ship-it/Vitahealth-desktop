package org.example.services;

import org.example.entities.Sujet;
import org.example.utils.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SujetService implements IService<Sujet> {

    private Connection connection;

    public SujetService() {
        connection = MyConnection.getInstance().getConnection();
    }

    @Override
    public void ajouter(Sujet sujet) {
        String query = "INSERT INTO sujet (titre, categorie, contenu, auteur, valid, date_creation) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, sujet.getTitre());
            ps.setString(2, sujet.getCategorie());
            ps.setString(3, sujet.getContenu());
            ps.setString(4, sujet.getAuteur());
            ps.setBoolean(5, sujet.isValid());
            ps.setTimestamp(6, sujet.getDateCreation());
            ps.executeUpdate();
            System.out.println("✅ Sujet ajouté : " + sujet.getTitre());
        } catch (SQLException e) {
            System.err.println("❌ Erreur ajout sujet : " + e.getMessage());
        }
    }

    @Override
    public void modifier(Sujet sujet) {
        String query = "UPDATE sujet SET titre=?, categorie=?, contenu=?, auteur=?, valid=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, sujet.getTitre());
            ps.setString(2, sujet.getCategorie());
            ps.setString(3, sujet.getContenu());
            ps.setString(4, sujet.getAuteur());
            ps.setBoolean(5, sujet.isValid());
            ps.setInt(6, sujet.getId());
            ps.executeUpdate();
            System.out.println("✅ Sujet modifié : id=" + sujet.getId());
        } catch (SQLException e) {
            System.err.println("❌ Erreur modification sujet : " + e.getMessage());
        }
    }

    @Override
    public void supprimer(int id) {
        String query = "DELETE FROM sujet WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("✅ Sujet supprimé : id=" + id);
        } catch (SQLException e) {
            System.err.println("❌ Erreur suppression sujet : " + e.getMessage());
        }
    }

    @Override
    public Sujet getById(int id) {
        String query = "SELECT * FROM sujet WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur getById sujet : " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Sujet> getAll() {
        List<Sujet> sujets = new ArrayList<>();
        String query = "SELECT * FROM sujet ORDER BY date_creation DESC";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                sujets.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur getAll sujets : " + e.getMessage());
        }
        return sujets;
    }

    // ── Fonctionnalités avancées ───────────────────────────────────────────────

    public List<Sujet> getByCategorie(String categorie) {
        List<Sujet> sujets = new ArrayList<>();
        String query = "SELECT * FROM sujet WHERE categorie=? ORDER BY date_creation DESC";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, categorie);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) sujets.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("❌ Erreur getByCategorie : " + e.getMessage());
        }
        return sujets;
    }

    public List<Sujet> rechercher(String motCle) {
        List<Sujet> sujets = new ArrayList<>();
        String query = "SELECT * FROM sujet WHERE titre LIKE ? ORDER BY date_creation DESC";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, "%" + motCle + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) sujets.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("❌ Erreur recherche sujet : " + e.getMessage());
        }
        return sujets;
    }

    public void valider(int id, boolean valid) {
        String query = "UPDATE sujet SET valid=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setBoolean(1, valid);
            ps.setInt(2, id);
            ps.executeUpdate();
            System.out.println("✅ Sujet id=" + id + " → valid=" + valid);
        } catch (SQLException e) {
            System.err.println("❌ Erreur validation sujet : " + e.getMessage());
        }
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private Sujet mapRow(ResultSet rs) throws SQLException {
        return new Sujet(
                rs.getInt("id"),
                rs.getString("titre"),
                rs.getString("categorie"),
                rs.getString("contenu"),
                rs.getString("auteur"),
                rs.getBoolean("valid"),
                rs.getTimestamp("date_creation")
        );
    }
}