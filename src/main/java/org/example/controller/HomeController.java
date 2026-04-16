package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.entities.Commentaire;
import org.example.entities.Sujet;
import org.example.entities.Utilisateur;
import org.example.services.CommentaireService;
import org.example.services.SujetService;
import org.example.utils.SessionManager;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class HomeController implements Initializable {

    @FXML private TextField titreField;      // kept to avoid FXML error – no longer used
    @FXML private TextArea contenuField;     // kept to avoid FXML error – no longer used
    @FXML private ComboBox<String> categorieCombo; // kept to avoid FXML error – no longer used
    @FXML private VBox feedContainer;

    private SujetService sujetService = new SujetService();
    private CommentaireService commentaireService = new CommentaireService();
    private Utilisateur currentUser = SessionManager.getInstance().getCurrentUser();
    private Set<Integer> likedPosts = new HashSet<>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy 'à' HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // ComboBox field is declared but not used in FXML - skip initialization
        if (categorieCombo != null) {
            categorieCombo.getItems().addAll("Cardiologie", "Neurologie", "Nutrition", "Pédiatrie", "Psychologie", "Général");
            categorieCombo.setValue("Général");
        }
        loadFeed(null);
    }

    // ======================== MODAL PUBLICATION ========================

    @FXML
    private void openCreateModal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CreatePostDialog.fxml"));
            Parent dialogRoot = loader.load();

            // Pass the current user to the dialog controller
            CreatePostController ctrl = loader.getController();
            ctrl.setCurrentUser(currentUser);

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initStyle(StageStyle.TRANSPARENT);
            dialog.initOwner(feedContainer.getScene().getWindow());
            dialog.setTitle("Créer une publication");

            StackPane overlay = new StackPane();
            overlay.setStyle("-fx-background-color: rgba(0,0,0,0.45);");
            overlay.setAlignment(Pos.CENTER);
            overlay.getChildren().add(dialogRoot);
            overlay.setOnMouseClicked(e -> {
                if (e.getTarget() == overlay) dialog.close();
            });

            Scene scene = new Scene(overlay, 700, 560);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            dialog.setScene(scene);
            dialog.showAndWait();

            // Refresh feed after dialog closes
            loadFeed(null);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur : impossible d'ouvrir l'éditeur.");
        }
    }

    // ======================== FEED MANAGEMENT ========================

    public void filterByCategorie(String categorie) {
        loadFeed(categorie);
    }

    public void search(String query) {
        List<Sujet> sujets = sujetService.rechercher(query);
        feedContainer.getChildren().clear();
        for (Sujet s : sujets) {
            feedContainer.getChildren().add(createPostCard(s));
        }
    }

    private void loadFeed(String categorie) {
        List<Sujet> sujets = (categorie == null) ? sujetService.getValidSubjects() : sujetService.getByCategorie(categorie);
        feedContainer.getChildren().clear();
        if (sujets.isEmpty()) {
            Label empty = new Label("Aucune publication pour le moment.");
            empty.setStyle("-fx-font-size: 16px; -fx-text-fill: #65676b; -fx-padding: 40;");
            feedContainer.getChildren().add(empty);
        } else {
            for (Sujet s : sujets) {
                feedContainer.getChildren().add(createPostCard(s));
            }
        }
    }

    // ======================== POST CARD UI ========================

    private VBox createPostCard(Sujet sujet) {
        VBox card = new VBox(12);
        card.getStyleClass().add("post-card");
        card.setPadding(new Insets(15));

        // Header
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        Label avatar = new Label("👤");
        avatar.setStyle("-fx-font-size: 44px;");

        VBox authorInfo = new VBox(2);
        Label authorName = new Label(sujet.getAuteurNom());
        authorName.getStyleClass().add("post-author");
        Label postDate = new Label(dateFormat.format(sujet.getDateCreation()));
        postDate.getStyleClass().add("post-date");
        authorInfo.getChildren().addAll(authorName, postDate);

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label catBadge = new Label("📌 " + sujet.getCategorie());
        catBadge.getStyleClass().add("category-badge");
        catBadge.setStyle(getCategoryStyle(sujet.getCategorie()));

        header.getChildren().addAll(avatar, authorInfo, spacer, catBadge);

        // Title
        Label title = new Label(sujet.getTitre());
        title.getStyleClass().add("post-title");
        title.setWrapText(true);

        // Content
        Text contentText = new Text(sujet.getContenu());
        contentText.getStyleClass().add("post-content");
        TextFlow contentFlow = new TextFlow(contentText);
        contentFlow.setLineSpacing(4);

        // Counters
        HBox counters = new HBox(20);
        counters.setAlignment(Pos.CENTER_LEFT);
        Label likeCountLbl = new Label(likedPosts.contains(sujet.getId()) ? "👍 1 J'aime" : "👍 0 J'aime");
        likeCountLbl.getStyleClass().add("counter-label");
        int nbCom = commentaireService.countBySujet(sujet.getId());
        Label comCountLbl = new Label("💬 " + nbCom + " commentaire" + (nbCom > 1 ? "s" : ""));
        comCountLbl.getStyleClass().add("counter-label");
        counters.getChildren().addAll(likeCountLbl, comCountLbl);

        Separator sep = new Separator();
        sep.setPadding(new Insets(5, 0, 5, 0));

        // Action bar
        HBox actionBar = new HBox(20);
        actionBar.setAlignment(Pos.CENTER_LEFT);

        Button likeBtn = new Button("👍 J'aime");
        likeBtn.getStyleClass().add("action-btn");
        if (likedPosts.contains(sujet.getId())) {
            likeBtn.getStyleClass().add("action-btn-active");
            likeBtn.setText("👍 Aimé");
        }
        likeBtn.setOnAction(e -> {
            if (likedPosts.contains(sujet.getId())) {
                likedPosts.remove(sujet.getId());
                likeBtn.getStyleClass().remove("action-btn-active");
                likeBtn.setText("👍 J'aime");
                likeCountLbl.setText("👍 0 J'aime");
            } else {
                likedPosts.add(sujet.getId());
                likeBtn.getStyleClass().add("action-btn-active");
                likeBtn.setText("👍 Aimé");
                likeCountLbl.setText("👍 1 J'aime");
            }
        });

        Button commentBtn = new Button("💬 Commenter");
        commentBtn.getStyleClass().add("action-btn");

        // Comment section
        VBox commentSection = new VBox(8);
        commentSection.setVisible(false);
        commentSection.setManaged(false);
        commentSection.setStyle("-fx-background-color: #f0f2f5; -fx-background-radius: 10; -fx-padding: 12;");

        VBox commentsList = new VBox(8);
        commentSection.getChildren().add(commentsList);

        HBox addCommentBox = new HBox(8);
        addCommentBox.setAlignment(Pos.CENTER_LEFT);
        TextField commentField = new TextField();
        commentField.setPromptText("Écrire un commentaire...");
        commentField.setStyle("-fx-background-radius: 20; -fx-padding: 8 15; -fx-background-color: white;");
        HBox.setHgrow(commentField, Priority.ALWAYS);
        Button sendBtn = new Button("Envoyer");
        sendBtn.getStyleClass().add("btn-primary");
        sendBtn.setOnAction(e -> {
            String txt = commentField.getText().trim();
            if (!txt.isEmpty()) {
                commentaireService.ajouter(new Commentaire(txt, currentUser.getId(), sujet.getId(), currentUser.getNom()));
                commentField.clear();
                loadComments(sujet.getId(), commentsList, comCountLbl);
            }
        });
        addCommentBox.getChildren().addAll(commentField, sendBtn);
        commentSection.getChildren().add(addCommentBox);

        commentBtn.setOnAction(e -> {
            boolean show = !commentSection.isVisible();
            commentSection.setVisible(show);
            commentSection.setManaged(show);
            if (show) loadComments(sujet.getId(), commentsList, comCountLbl);
        });

        actionBar.getChildren().addAll(likeBtn, commentBtn);

        // Admin actions
        if (currentUser.getId() == sujet.getAuteurId() || SessionManager.getInstance().isAdmin()) {
            Button editBtn = new Button("✏️");
            editBtn.getStyleClass().add("icon-btn");
            editBtn.setOnAction(e -> editSujet(sujet));
            Button deleteBtn = new Button("🗑️");
            deleteBtn.getStyleClass().add("icon-btn");
            deleteBtn.setOnAction(e -> deleteSujet(sujet));
            HBox adminBox = new HBox(5, editBtn, deleteBtn);
            adminBox.setAlignment(Pos.CENTER_RIGHT);
            actionBar.getChildren().add(adminBox);
        }

        card.getChildren().addAll(header, title, contentFlow, counters, sep, actionBar, commentSection);
        return card;
    }

    private void loadComments(int sujetId, VBox container, Label counterLbl) {
        container.getChildren().clear();
        List<Commentaire> comments = commentaireService.getBySujetId(sujetId);
        for (Commentaire c : comments) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle("-fx-background-color: white; -fx-background-radius: 18; -fx-padding: 10 14;");
            Label av = new Label("👤");
            av.setStyle("-fx-font-size: 24px;");
            VBox txt = new VBox(3);
            Label name = new Label(c.getAuteurNom());
            name.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
            Label cont = new Label(c.getContenu());
            cont.setWrapText(true);
            cont.setStyle("-fx-font-size: 14px;");
            txt.getChildren().addAll(name, cont);
            HBox.setHgrow(txt, Priority.ALWAYS);
            row.getChildren().addAll(av, txt);
            container.getChildren().add(row);
        }
        counterLbl.setText("💬 " + comments.size() + " commentaire" + (comments.size() > 1 ? "s" : ""));
    }

    private void editSujet(Sujet sujet) {
        TextInputDialog dialog = new TextInputDialog(sujet.getContenu());
        dialog.setTitle("Modifier la publication");
        dialog.setHeaderText("Modifier le contenu");
        dialog.setContentText("Nouveau contenu :");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newContent -> {
            sujet.setContenu(newContent);
            sujetService.modifier(sujet);
            loadFeed(null);
        });
    }

    private void deleteSujet(Sujet sujet) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer définitivement cette publication ?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                sujetService.supprimer(sujet.getId());
                loadFeed(null);
            }
        });
    }

    private String getCategoryStyle(String cat) {
        return switch (cat) {
            case "Cardiologie" -> "-fx-background-color: #fdecea; -fx-text-fill: #c62828;";
            case "Neurologie"  -> "-fx-background-color: #ede7f6; -fx-text-fill: #4527a0;";
            case "Nutrition"   -> "-fx-background-color: #e6f4ea; -fx-text-fill: #1e7e34;";
            case "Pédiatrie"   -> "-fx-background-color: #e3f2fd; -fx-text-fill: #1565c0;";
            case "Psychologie" -> "-fx-background-color: #fff3e0; -fx-text-fill: #e65100;";
            default            -> "-fx-background-color: #f3e5f5; -fx-text-fill: #6a1b9a;";
        };
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK).showAndWait();
    }
}