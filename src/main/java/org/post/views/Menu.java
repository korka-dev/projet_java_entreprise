package org.post.views;

import java.sql.SQLException;
import java.util.Scanner;

public class Menu {
    public  static void printMenu(){

        System.out.println("**********************************************");
        System.out.println("******** GESTIONNAIRE D'UNE ENTREPRISE *******");
        System.out.println("**********************************************");
        System.out.println("---------------- EMPLOYERS ----------------");
        System.out.println(" \t\t1. Insérer Employé");
        System.out.println(" \t\t2. Affichage Employés");
        System.out.println(" \t\t3. Affichage Infos Employé");
        System.out.println(" \t\t4. Affichage Salaire Moyenne");
        System.out.println(" \t\t5. Authentication");
        System.out.println(" \t\t6. Rechercher Employés selon un poste");
        System.out.println(" \t\t7. Affichage Salaires selon un poste");
        System.out.println(" \t\t8. Statistique");
        System.out.println("--------------------------------------------");
        System.out.println(" \t\t9. Quitter");
        System.out.println("*********************************************");

    }

    public static void run() throws SQLException {

        Scanner scanner = new Scanner(System.in);
        while (true) {
            printMenu();

            System.out.print("Choix >: ");
            int choice = scanner.nextInt();


            switch (choice) {

                case 1:
                    User.askCreateEmploy();
                    break;
                case 2:
                    User.askGetAllEmployees();
                    break;
                case 3:
                    User.askDisplayEmployeInfo();
                    break;
                case 4:
                    User.displayAverageSalary();
                    break;
                case 5:
                    Auth.askAuthenticate();
                    break;

                case 6:
                    User.searchEmployeesByPost();
                    break;

                case 7:
                    User.askAndDisplaySalariesByPost();
                    break;
                case 8:
                    User.displayCompanyStatistics();
                    break;

                case 9:
                    System.out.println("Terminé ....");
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("Choix Invalide");

            }
        }

    }
}
