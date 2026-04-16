package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import org.example.entities.Utilisateur;
import org.example.services.CommentaireService;
import org.example.services.SujetService;
import org.example.utils.SessionManager;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private StackPane contentArea;
    @FXML private TextField searchField;
    @FXML private Label usernameLabel;
    @FXML private Label roleLabel;
    @FXML private Label statusLabel;
    @FXML private Label totalSujetsLabel;
    @FXML private Label totalComLabel;
    @FXML private Label navUsernameLabel;
    @FXML private Label navRoleLabel;
    @FXML private Button btnHome, btnForum, btnAdmin, btnProfile;

    private SujetService sujetService = new SujetService();
    private CommentaireService commentaireService = new CommentaireService();
    private Utilisateur currentUser;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            usernameLabel.setText(currentUser.getNom());
            roleLabel.setText(currentUser.getRole());
            navUsernameLabel.setText(currentUser.getNom());
            navRoleLabel.setText(currentUser.getRole());
        }
        updateStats();
        showHome();
    }

    private void updateStats() {
        totalSujetsLabel.setText(String.valueOf(sujetService.getValidSubjects().size()));
        totalComLabel.setText(String.valueOf(commentaireService.getAll().size()));
    }

    private void loadView(String fxml) {
        try {
            Node view = FXMLLoader.load(getClass().getResource("/views/" + fxml));
            contentArea.getChildren().setAll(view);
            updateStats();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML public void showHome() {
        loadView("HomeView.fxml");
        setActiveButton(btnHome);
        statusLabel.setText("🏠 Accueil");
    }

    @FXML public void showForum() {
        loadView("Forum.fxml");
        setActiveButton(btnForum);
        statusLabel.setText("💬 Forum");
    }

    @FXML public void showAdmin() {
        if (SessionManager.getInstance().isAdmin()) {
            loadView("AdminView.fxml");
            setActiveButton(btnAdmin);
            statusLabel.setText("⚙️ Administration");
        } else {
            statusLabel.setText("⛔ Accès réservé aux administrateurs");
        }
    }

    @FXML public void showProfile() {
        statusLabel.setText("👤 Profil de " + currentUser.getNom());
    }

    @FXML public void filterByNutrition() {
        loadFilteredView("Nutrition");
    }

    @FXML public void filterByCardio() {
        loadFilteredView("Cardiologie");
    }

    @FXML public void filterByPsycho() {
        loadFilteredView("Psychologie");
    }

    @FXML public void filterByPediatrie() {
        loadFilteredView("Pédiatrie");
    }

    @FXML public void showAllSubjects() {
        loadView("HomeView.fxml");
    }

    private void loadFilteredView(String categorie) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/HomeView.fxml"));
            Node view = loader.load();
            HomeController controller = loader.getController();
            controller.filterByCategorie(categorie);
            contentArea.getChildren().setAll(view);
            statusLabel.setText("🔍 Catégorie : " + categorie);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML public void handleSearch() {
        String query = searchField.getText().trim();
        if (!query.isEmpty()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/HomeView.fxml"));
                Node view = loader.load();
                HomeController controller = loader.getController();
                controller.search(query);
                contentArea.getChildren().setAll(view);
                statusLabel.setText("🔍 Résultats pour : " + query);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showHome();
        }
    }

    @FXML public void openCreateModal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CreatePostDialog.fxml"));
            loader.load();
            statusLabel.setText("📝 Nouvelle publication");
        } catch (IOException e) {
            statusLabel.setText("❌ Erreur lors de l'ouverture du formulaire");
            e.printStackTrace();
        }
    }

    @FXML public void logout() {
        statusLabel.setText("👋 Déconnexion simulée");
    }

    private void setActiveButton(Button active) {
        for (Button b : new Button[]{btnHome, btnForum, btnAdmin, btnProfile}) {
            if (b != null) b.getStyleClass().remove("nav-btn-active");
        }
        if (active != null) active.getStyleClass().add("nav-btn-active");
    }
}