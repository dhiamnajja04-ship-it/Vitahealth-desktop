package org.example.services;

import org.example.entities.Sujet;
import org.example.utils.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SujetService implements IService<Sujet> {
    private Connection connection = MyConnection.getInstance().getConnection();

    @Override
    public void ajouter(Sujet s) {
        String sql = "INSERT INTO sujet (titre, categorie, contenu, auteur, auteur_id, valid) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, s.getTitre());
            ps.setString(2, s.getCategorie());
            ps.setString(3, s.getContenu());
            ps.setString(4, s.getAuteurNom() != null ? s.getAuteurNom() : "");
            ps.setInt(5, s.getAuteurId());
            ps.setBoolean(6, s.isValid());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void modifier(Sujet s) {
        String sql = "UPDATE sujet SET titre=?, categorie=?, contenu=?, valid=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, s.getTitre());
            ps.setString(2, s.getCategorie());
            ps.setString(3, s.getContenu());
            ps.setBoolean(4, s.isValid());
            ps.setInt(5, s.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void supprimer(int id) {
        String sql = "DELETE FROM sujet WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Sujet getById(int id) {
        String sql = "SELECT s.*, u.nom as auteur_nom, u.avatar_url FROM sujet s " +
                "JOIN utilisateur u ON s.auteur_id = u.id WHERE s.id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Sujet> getAll() {
        List<Sujet> list = new ArrayList<>();
        String sql = "SELECT s.*, u.nom as auteur_nom, u.avatar_url FROM sujet s " +
                "JOIN utilisateur u ON s.auteur_id = u.id ORDER BY s.date_creation DESC";
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Récupère uniquement les sujets validés pour le feed
    public List<Sujet> getValidSubjects() {
        List<Sujet> list = new ArrayList<>();
        String sql = "SELECT s.*, u.nom as auteur_nom, u.avatar_url FROM sujet s " +
                "JOIN utilisateur u ON s.auteur_id = u.id WHERE s.valid = true ORDER BY s.date_creation DESC";
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Sujet> getByCategorie(String categorie) {
        List<Sujet> list = new ArrayList<>();
        String sql = "SELECT s.*, u.nom as auteur_nom, u.avatar_url FROM sujet s " +
                "JOIN utilisateur u ON s.auteur_id = u.id WHERE s.categorie=? AND s.valid = true ORDER BY s.date_creation DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, categorie);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Sujet> rechercher(String motCle) {
        List<Sujet> list = new ArrayList<>();
        String sql = "SELECT s.*, u.nom as auteur_nom, u.avatar_url FROM sujet s " +
                "JOIN utilisateur u ON s.auteur_id = u.id WHERE s.titre LIKE ? AND s.valid = true ORDER BY s.date_creation DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "%" + motCle + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void valider(int id, boolean valid) {
        String sql = "UPDATE sujet SET valid=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setBoolean(1, valid);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Sujet mapRow(ResultSet rs) throws SQLException {
        Sujet s = new Sujet();
        s.setId(rs.getInt("id"));
        s.setTitre(rs.getString("titre"));
        s.setCategorie(rs.getString("categorie"));
        s.setContenu(rs.getString("contenu"));
        s.setAuteurId(rs.getInt("auteur_id"));
        s.setValid(rs.getBoolean("valid"));
        s.setDateCreation(rs.getTimestamp("date_creation"));
        s.setAuteurNom(rs.getString("auteur_nom"));
        s.setAuteurAvatar(rs.getString("avatar_url"));
        return s;
    }
}