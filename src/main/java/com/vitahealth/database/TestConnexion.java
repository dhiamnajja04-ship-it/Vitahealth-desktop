package com.vitahealth.database;

public class TestConnexion {
    public static void main(String[] args) {
        System.out.println("Test de connexion MySQL...");

        // Tester la connexion
        if (DatabaseConnection.getConnection() != null) {
            System.out.println("✅ Connexion réussie !");
        } else {
            System.out.println("❌ Échec de connexion !");
        }

        // Fermer la connexion
        DatabaseConnection.closeConnection();
    }
}