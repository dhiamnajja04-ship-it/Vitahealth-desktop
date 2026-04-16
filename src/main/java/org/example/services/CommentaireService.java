package org.example.services;

import org.example.entities.Commentaire;
import org.example.utils.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentaireService implements IService<Commentaire> {
    private Connection connection = MyConnection.getInstance().getConnection();

    @Override
    public void ajouter(Commentaire c) {
        String sql = "INSERT INTO commentaire (contenu, auteur, auteur_id, sujet_id) VALUES (?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, c.getContenu());
            ps.setString(2, c.getAuteurNom() != null ? c.getAuteurNom() : "");
            ps.setInt(3, c.getAuteurId());
            ps.setInt(4, c.getSujetId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void modifier(Commentaire c) {
        String sql = "UPDATE commentaire SET contenu=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, c.getContenu());
            ps.setInt(2, c.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void supprimer(int id) {
        String sql = "DELETE FROM commentaire WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Commentaire getById(int id) {
        String sql = "SELECT c.*, u.nom as auteur_nom, u.avatar_url FROM commentaire c " +
                "JOIN utilisateur u ON c.auteur_id = u.id WHERE c.id=?";
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
    public List<Commentaire> getAll() {
        List<Commentaire> list = new ArrayList<>();
        String sql = "SELECT c.*, u.nom as auteur_nom, u.avatar_url FROM commentaire c " +
                "JOIN utilisateur u ON c.auteur_id = u.id ORDER BY c.date_creation";
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Commentaire> getBySujetId(int sujetId) {
        List<Commentaire> list = new ArrayList<>();
        String sql = "SELECT c.*, u.nom as auteur_nom, u.avatar_url FROM commentaire c " +
                "JOIN utilisateur u ON c.auteur_id = u.id WHERE c.sujet_id=? ORDER BY c.date_creation";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, sujetId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countBySujet(int sujetId) {
        String sql = "SELECT COUNT(*) FROM commentaire WHERE sujet_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, sujetId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Commentaire mapRow(ResultSet rs) throws SQLException {
        Commentaire c = new Commentaire();
        c.setId(rs.getInt("id"));
        c.setContenu(rs.getString("contenu"));
        c.setAuteurId(rs.getInt("auteur_id"));
        c.setDateCreation(rs.getTimestamp("date_creation"));
        c.setSujetId(rs.getInt("sujet_id"));
        c.setAuteurNom(rs.getString("auteur_nom"));
        c.setAuteurAvatar(rs.getString("avatar_url"));
        return c;
    }
}