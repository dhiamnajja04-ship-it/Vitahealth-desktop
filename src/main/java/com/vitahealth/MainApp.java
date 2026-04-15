package com.vitahealth;

import com.vitahealth.controllers.PatientController;
import com.vitahealth.database.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        showLoginDialog(primaryStage);
    }

    private void showLoginDialog(Stage primaryStage) {
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Connexion");
        dialog.setHeaderText("VitaHealth - Authentification");

        ButtonType loginButtonType = new ButtonType("Se connecter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField emailField = new TextField();
        emailField.setPromptText("email@exemple.com");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Mot de passe");

        grid.add(new Label("Email:"), 0, 0);
        grid.add(emailField, 1, 0);
        grid.add(new Label("Mot de passe:"), 0, 1);
        grid.add(passwordField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new String[]{emailField.getText(), passwordField.getText()};
            }
            return null;
        });

        Optional<String[]> result = dialog.showAndWait();
        result.ifPresent(credentials -> {
            String email = credentials[0];
            String password = credentials[1];
            authenticateAndRedirect(primaryStage, email, password);
        });
    }

    private void authenticateAndRedirect(Stage stage, String email, String password) {
        String sql = "SELECT id, roles, password FROM user WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                if (BCrypt.checkpw(password, hashedPassword)) {
                    int userId = rs.getInt("id");
                    String rolesJson = rs.getString("roles");
                    String role = extractRoleFromJson(rolesJson);
                    System.out.println("Rôle extrait: " + role);
                    if ("ROLE_PATIENT".equalsIgnoreCase(role)) {
                        loadPatientInterface(stage, userId, email);
                    } else if ("ROLE_MEDECIN".equalsIgnoreCase(role)) {
                        loadMedecinInterface(stage, userId, email);
                    } else {
                        showAlert("Erreur", "Rôle non supporté : " + role, Alert.AlertType.ERROR);
                        showLoginDialog(stage);
                    }
                } else {
                    showAlert("Erreur", "Mot de passe incorrect", Alert.AlertType.ERROR);
                    showLoginDialog(stage);
                }
            } else {
                showAlert("Erreur", "Email non trouvé", Alert.AlertType.ERROR);
                showLoginDialog(stage);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur technique : " + e.getMessage(), Alert.AlertType.ERROR);
            showLoginDialog(stage);
        }
    }

    private String extractRoleFromJson(String json) {
        if (json == null || json.isEmpty()) return "";
        int start = json.indexOf('"');
        int end = json.lastIndexOf('"');
        if (start != -1 && end != -1 && start < end) {
            return json.substring(start + 1, end);
        }
        return json;
    }

    private void loadPatientInterface(Stage stage, int userId, String email) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/patient_dashboard.fxml"));
            Parent root = loader.load();
            PatientController controller = loader.getController();
            controller.setCurrentUser(userId, email);
            stage.setTitle("VitaHealth - Espace Patient (" + email + ")");
            stage.setScene(new Scene(root, 1000, 700));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger l'interface patient", Alert.AlertType.ERROR);
            showLoginDialog(stage);
        }
    }

    private void loadMedecinInterface(Stage stage, int userId, String email) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/medecin_prescriptions.fxml"));
            Parent root = loader.load();
            stage.setTitle("VitaHealth - Espace Médecin (" + email + ")");
            stage.setScene(new Scene(root, 1100, 700));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger l'interface médecin", Alert.AlertType.ERROR);
            showLoginDialog(stage);
        }
    }

    private void showAlert(String titre, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}