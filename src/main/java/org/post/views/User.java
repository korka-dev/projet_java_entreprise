package org.post.views;

import org.post.dao.UserDAO;
import org.post.utils.Printing;

import java.sql.SQLException;
import java.sql.Date;

import java.util.List;
import java.util.Scanner;

public class User {

    public static final String RESET = "\u001B[0m";
    public static final String GREEN = "\u001B[32m";
    public static final String RED = "\u001B[31m";

    static UserDAO userTable;

    static {
        try {
            userTable= new UserDAO("employers");

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public static void askCreateEmploy() throws SQLException {
        System.out.println("\t\t*** Insertion d'un employé ***");
        Scanner scanner = new Scanner(System.in);

        System.out.print("\u001B[36mEntrer votre nom complet >: \u001B[0m");
        String nomComplet = scanner.nextLine();

        System.out.print("\u001B[36mEntrer votre email >: \u001B[0m");
        String email = scanner.nextLine();

        if (userTable.getByEmail(email) != null) {
            System.out.println("\u001B[31mL'email existe déjà. Veuillez utiliser un autre email.\u001B[0m");
            return;
        }

        System.out.print("\u001B[36mEntrer votre mot de passe >: \u001B[0m");
        String password = scanner.nextLine();

        System.out.print("\u001B[36mEntrer votre âge >: \u001B[0m");
        int age = scanner.nextInt();

        System.out.print("\u001B[36mEntrer votre date d'entrée en service (format aaaa-mm-jj) >: \u001B[0m");
        String serviceDateString = scanner.next().trim();

        if (serviceDateString.isEmpty()) {
            System.out.println(RED + "La date de service ne peut pas être vide. Veuillez réessayer." + RESET);
            return;
        }

        Date service = null;
        try {
            service = Date.valueOf(serviceDateString);
        } catch (IllegalArgumentException e) {
            System.out.println(RED + "Format de date incorrect. Veuillez utiliser le format aaaa-mm-jj." + RESET);
            return;
        }

        String poste;
        boolean isValidPoste;

        do {
            System.out.print("\u001B[36mEntrer votre poste (vendeur/representant/producteur/manutentionnaire) >: \u001B[0m");
            poste = scanner.next().toLowerCase();

            isValidPoste = poste.equals("vendeur") || poste.equals("representant") || poste.equals("producteur") || poste.equals("manutentionnaire");

            if (!isValidPoste) {
                System.out.println(RED + "Poste invalide. Veuillez choisir parmi vendeur, representant, producteur, ou manutentionnaire." + RESET);
            }
        } while (!isValidPoste);

        double chiffreAffaire = 0;
        int nombreUnitesProduites = 0;
        int nombreHeuresTravaillees = 0;
        boolean manipuleProduitsDangereux = false;

        if (poste.equals("vendeur") || poste.equals("representant")) {
            System.out.print("\u001B[36mEntrer le chiffre d'affaire réalisé ce mois-ci >: \u001B[0m");
            chiffreAffaire = scanner.nextDouble();
        } else if (poste.equals("producteur")) {
            System.out.print("\u001B[36mEntrer le nombre d'unités produites ce mois-ci >: \u001B[0m");
            nombreUnitesProduites = scanner.nextInt();
        } else {
            System.out.print("\u001B[36mEntrer le nombre d'heures travaillées ce mois-ci >: \u001B[0m");
            nombreHeuresTravaillees = scanner.nextInt();

            System.out.print("\u001B[36mManipulez-vous des produits dangereux ? (oui/non) >: \u001B[0m");
            manipuleProduitsDangereux = scanner.nextBoolean();
        }

        double salaire = calculateSalary(poste, chiffreAffaire, nombreUnitesProduites, nombreHeuresTravaillees, manipuleProduitsDangereux);

        System.out.println(" Création d'employé en cours ......");

        if (userTable.insertEmploy(nomComplet, email, password, age, (java.sql.Date) service, salaire, poste)) {
            System.out.println(GREEN + "Employé créé avec succès!" + RESET);
        } else {
            System.out.println(RED + "Echec de la création d'un employé" + RESET);
        }
    }

    public static double calculateSalary(String poste, double chiffreAffaire, int nombreUnitesProduites, int nombreHeuresTravaillees, boolean manipuleProduitsDangereux) {
        double salaire = 0.0;

        switch (poste.toLowerCase()) {
            case "vendeur":
                salaire = chiffreAffaire * 0.20 + 400;
                break;
            case "representant":
                salaire = chiffreAffaire * 0.20 + 800;
                break;
            case "producteur":
                salaire = nombreUnitesProduites * 5;
                break;
            case "manutentionnaire":
                salaire = nombreHeuresTravaillees * 65;
                break;
            default:
                System.out.println("Poste non reconnue.");
                return salaire;
        }

        if (manipuleProduitsDangereux) {
            salaire += 200;
        }

        return salaire;
    }


    public static void askGetAllEmployees() throws SQLException {
        System.out.println("\t\t**** Affichage des employers ******");
        System.out.println("Affichage des employers en cours ....");

        List<org.post.models.User> users = userTable.getAllEmployes();
        System.out.println(".... " + "(" + users.size() + ") resultats....");
        Printing.print(users);

    }

    public static void searchEmployeesByPost() throws SQLException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("\u001B[36mEntrer le poste pour la recherche (vendeur/representant/producteur/manutentionnaire) >: \u001B[0m");
        String poste = scanner.next().toLowerCase();

        if (!isValidPoste(poste)) {
            System.out.println(RED + "Poste invalide. Veuillez choisir parmi vendeur, representant, producteur, ou manutentionnaire." + RESET);
            return;
        }

        List<org.post.models.User> employees = userTable.getEmployeesByPost(poste);

        if (employees.isEmpty()) {
            System.out.println("Aucun employé trouvé pour le poste de " + poste);
        } else {
            System.out.println("Liste des employés pour le poste de " + poste + " :");
            for (org.post.models.User employer : employees) {
                System.out.println(employer);
            }
        }
    }

    private static boolean isValidPoste(String poste) {
        return poste.equals("vendeur") || poste.equals("representant") || poste.equals("producteur") || poste.equals("manutentionnaire");
    }

    public static void askAndDisplaySalariesByPost() throws SQLException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("\u001B[36mEntrer le poste à afficher les salaires >: \u001B[0m");
        String poste = scanner.next().toLowerCase();

        try {
            userTable.displaySalariesByPost(poste);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void askDisplayEmployeInfo() throws SQLException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("\u001B[36mEntrer l'email de l'employé >: \u001B[0m");
        String email = scanner.nextLine();

        userTable.displayEmployeeInfoByEmail(email);
    }

    public static void displayAverageSalary() {
        try {
            double averageSalary = userTable.calculateAverageSalary();
            System.out.print("\u001B[36mLa moyenne des salaires est : ");
            System.out.println(averageSalary);
            System.out.print("\u001B[0m");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void displayCompanyStatistics() {

        try {
            int totalEmployees = userTable.getTotalEmployees();
            double averageSalary = userTable.calculateAverageSalary();
            int salesEmployees = userTable.getEmployeesByPost("vendeur").size();
            int productionEmployees = userTable.getEmployeesByPost("producteur").size();
            int handlingEmployees = userTable.getEmployeesByPost("manutentionnaire").size();

            System.out.println("\n\033[1;36mStatistiques de l'entreprise\033[0m");
            
            System.out.print("\u001B[36mNombre total d'employés :  ");
            System.out.println(totalEmployees);
            System.out.print("\u001B[0m");

            System.out.print("\u001B[36mMoyenne des salaires :  ");
            System.out.println(averageSalary);
            System.out.print("\u001B[0m");


            System.out.print("\u001B[36mNombre d'employés de vente :  ");
            System.out.println(salesEmployees);
            System.out.print("\u001B[0m");

            System.out.print("\u001B[36mNombre d'employés de production :  ");
            System.out.println(productionEmployees);
            System.out.print("\u001B[0m");

            System.out.print("\u001B[36mNombre d'employés de manutention  :  ");
            System.out.println(handlingEmployees);
            System.out.print("\u001B[0m");



        } catch (SQLException e) {
            e.printStackTrace();
        }
    }









}

