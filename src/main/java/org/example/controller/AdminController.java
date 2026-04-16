package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.example.entities.Sujet;
import org.example.entities.Utilisateur;
import org.example.services.SujetService;
import org.example.services.UtilisateurService;

import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    // Stats
    @FXML private Label totalUsersLabel;
    @FXML private Label activeUsersLabel;
    @FXML private Label pendingReviewsLabel;
    @FXML private Label systemHealthLabel;
    @FXML private Label gainScoreLabel;
    @FXML private Label applicabilityLabel;
    @FXML private ProgressBar gainScoreBar;
    @FXML private ProgressBar applicabilityBar;

    // Table Users
    @FXML private TableView<Utilisateur> userTable;
    @FXML private TableColumn<Utilisateur, String> userNameCol;
    @FXML private TableColumn<Utilisateur, String> userRoleCol;
    @FXML private TableColumn<Utilisateur, String> userStatusCol;
    @FXML private TableColumn<Utilisateur, String> userConnectivityCol;
    @FXML private TableColumn<Utilisateur, String> userLastActiveCol;

    // Table Publications (sujets)
    @FXML private TableView<Sujet> publicationTable;
    @FXML private TableColumn<Sujet, String> pubTitleCol;
    @FXML private TableColumn<Sujet, String> pubAuthorCol;
    @FXML private TableColumn<Sujet, String> pubCategoryCol;
    @FXML private TableColumn<Sujet, Timestamp> pubDateCol;
    @FXML private TableColumn<Sujet, Void> pubActionsCol; // boutons d'action

    private UtilisateurService utilisateurService = new UtilisateurService();
    private SujetService sujetService = new SujetService();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configureUserTable();
        configurePublicationTable();
        loadStats();
        loadUsers();
        loadPublications();

        // Valeurs fixes pour l'intelligence report (peut être dynamique plus tard)
        gainScoreLabel.setText("100%");
        gainScoreBar.setProgress(1.0);
        applicabilityLabel.setText("60%");
        applicabilityBar.setProgress(0.6);
        systemHealthLabel.setText("99.9%");
    }

    private void configureUserTable() {
        userNameCol.setCellValueFactory(cellData -> {
            Utilisateur u = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(u.getNom() + " (" + u.getEmail() + ")");
        });
        userRoleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        userStatusCol.setCellValueFactory(cellData -> {
            Utilisateur u = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(u.isOnline() ? "Active" : "Inactive");
        });
        userConnectivityCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("Web Desktop"));
        userLastActiveCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("Just now"));
    }

    private void configurePublicationTable() {
        pubTitleCol.setCellValueFactory(new PropertyValueFactory<>("titre"));
        pubAuthorCol.setCellValueFactory(new PropertyValueFactory<>("auteurNom"));
        pubCategoryCol.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        pubDateCol.setCellValueFactory(new PropertyValueFactory<>("dateCreation"));
        pubDateCol.setCellFactory(col -> new TableCell<Sujet, Timestamp>() {
            @Override
            protected void updateItem(Timestamp item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(dateFormat.format(item));
            }
        });

        // Colonne d'actions avec boutons Valider/Rejeter
        pubActionsCol.setCellFactory(col -> new TableCell<Sujet, Void>() {
            private final Button validateBtn = new Button("✓");
            private final Button rejectBtn = new Button("✗");
            private final HBox pane = new HBox(5, validateBtn, rejectBtn);

            {
                validateBtn.getStyleClass().add("icon-btn");
                rejectBtn.getStyleClass().add("icon-btn");
                validateBtn.setOnAction(e -> {
                    Sujet sujet = getTableView().getItems().get(getIndex());
                    sujetService.valider(sujet.getId(), true);
                    loadPublications();
                    loadStats();
                });
                rejectBtn.setOnAction(e -> {
                    Sujet sujet = getTableView().getItems().get(getIndex());
                    sujetService.supprimer(sujet.getId());
                    loadPublications();
                    loadStats();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                }
            }
        });
    }

    private void loadStats() {
        List<Utilisateur> users = utilisateurService.getAll();
        totalUsersLabel.setText(String.valueOf(users.size()));
        long active = users.stream().filter(Utilisateur::isOnline).count();
        activeUsersLabel.setText(String.valueOf(active));

        List<Sujet> sujets = sujetService.getAll();
        long pending = sujets.stream().filter(s -> !s.isValid()).count();
        pendingReviewsLabel.setText(String.valueOf(pending));
    }

    private void loadUsers() {
        ObservableList<Utilisateur> users = FXCollections.observableArrayList(utilisateurService.getAll());
        userTable.setItems(users);
    }

    private void loadPublications() {
        ObservableList<Sujet> sujets = FXCollections.observableArrayList(sujetService.getAll());
        publicationTable.setItems(sujets);
    }

    // Méthodes appelées depuis les boutons externes (optionnel)
    @FXML private void handleValidate() {}
    @FXML private void handleReject() {}
    @FXML private void refreshTable() {
        loadPublications();
        loadUsers();
        loadStats();
    }
}