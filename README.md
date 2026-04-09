# 🌟 VitaHealth – Plateforme de suivi médical intelligent

**VitaHealth** est une application console en Java permettant aux patients, médecins et administrateurs de gérer leurs profils, leurs rendez‑vous et leurs paramètres de santé.  
Le projet illustre l’utilisation de **JDBC**, des **Stream API** et du **hashage BCrypt** dans le cadre d’un cursus académique.

---

## 📚 Fonctionnalités

### 👤 Patient
- Inscription / connexion sécurisée
- Consultation et modification du profil
- Saisie des paramètres santé (poids, taille, glycémie, tension)
- Recommandation personnalisée basée sur l’IMC et la glycémie
- Prise de rendez‑vous avec un médecin
- Consultation de l’historique des rendez‑vous

### 👨‍⚕️ Médecin
- Gestion des rendez‑vous (confirmation, annulation, clôture)
- Liste des patients ayant consulté
- Recherche de patients par nom ou email

### 👑 Administrateur
- Gestion complète des utilisateurs (CRUD)
- Activation / désactivation de comptes
- Recherche avancée (nom, email, rôle, spécialité) – **SQL et Stream API**
- Tri des utilisateurs (nom, email, rôle, ID) – **Stream API**
- Statistiques (nombre par rôle, moyenne des poids) – **Stream API**
- Export des utilisateurs au format CSV

---

## 🛠️ Technologies utilisées

| Catégorie       | Technologie                              |
|----------------|------------------------------------------|
| Langage         | Java 17                                  |
| Base de données | MySQL 8.0                                |
| Serveur local   | XAMPP (phpMyAdmin)                       |
| Connectivité    | JDBC + Driver MySQL Connector/J          |
| Sécurité        | BCrypt (hashage des mots de passe)       |
| Build tool      | Maven                                    |
| Versioning      | Git / GitHub                             |
| IDE             | IntelliJ IDEA                            |

---

## 🚀 Installation et exécution

### Prérequis
- Java 17 ou supérieur
- MySQL (XAMPP ou installation native)
- Git (optionnel)

### Étapes

1. **Cloner le dépôt**
   ```bash
   git clone https://github.com/dhiamnajja04-ship-it/Vitahealth-desktop.git
   cd Vitahealth-desktop
