package org.example.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyConnection {

    private final String URL      = "jdbc:mysql://localhost:3306/healthcare_forum?useSSL=false&serverTimezone=UTC";
    private final String USER     = "root";
    private final String PASSWORD = "";  // ← ton mot de passe XAMPP ici si besoin

    private Connection connection;
    private static MyConnection instance;

    private MyConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connexion MySQL établie.");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver MySQL introuvable : " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("❌ Erreur connexion MySQL : " + e.getMessage());
            System.err.println("👉 Vérifiez que XAMPP MySQL est démarré !");
        }
    }

    public static MyConnection getInstance() {
        if (instance == null) {
            instance = new MyConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            // Reconnexion automatique si connexion perdue
            if (connection == null || connection.isClosed()) {
                System.out.println("🔄 Reconnexion à MySQL...");
                instance = new MyConnection();
            }
        } catch (SQLException e) {
            System.err.println("❌ Vérification connexion échouée : " + e.getMessage());
        }
        return connection;
    }
}