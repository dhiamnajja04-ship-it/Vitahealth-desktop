package com.vitahealth;

import org.mindrot.jbcrypt.BCrypt;

public class test {
    public static void main(String[] args) {
        // Le hash de votre base
        String hashDB = "$2y$13$dWc9anqvIhb5IresL16H1.22yH6mlcSpopgt35B1Bu/ux82tZ2o8S";

        // Testez avec "1234"
        boolean match1234 = BCrypt.checkpw("1234", hashDB);
        System.out.println("Test avec '1234': " + match1234);

        // Testez avec "patient"
        boolean matchPatient = BCrypt.checkpw("patient", hashDB);
        System.out.println("Test avec 'patient': " + matchPatient);

        // Testez avec "password"
        boolean matchPassword = BCrypt.checkpw("password", hashDB);
        System.out.println("Test avec 'password': " + matchPassword);

        // Générer un nouveau hash pour "1234"
        String newHash = BCrypt.hashpw("1234", BCrypt.gensalt(13));
        System.out.println("Nouveau hash pour '1234': " + newHash);
    }
}