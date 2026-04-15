package com.vitahealth;

import org.mindrot.jbcrypt.BCrypt;

public class GenerateHash {
    public static void main(String[] args) {
        String mdp = "1234";
        String hash = BCrypt.hashpw(mdp, BCrypt.gensalt(13));
        System.out.println("Hash pour '1234': " + hash);
        System.out.println("Longueur: " + hash.length());

        // Vérifier que le hash fonctionne
        boolean test = BCrypt.checkpw("1234", hash);
        System.out.println("Test: " + test);
    }
}