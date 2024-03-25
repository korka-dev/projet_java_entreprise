package org.post.views;

import org.post.config.Settings;
import org.post.dao.UserDAO;
import org.post.models.User;
import org.post.utils.Pair;

import java.sql.SQLException;
import java.util.Scanner;

public class Auth {

    public static final String RESET = "\u001B[0m";
    public static final String GREEN = "\u001B[32m";
    public static final String RED = "\u001B[31m";
    static UserDAO userTable;


    static {
        try {
            userTable = new UserDAO("employers");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public static void askAuthenticate() throws SQLException {

        Scanner scanner = new Scanner(System.in);

        System.out.print("\u001B[36mEntrer votre email >: \u001B[0m");
        String email = scanner.nextLine();

        System.out.print("\u001B[36mEntrer votre password >: \u001B[0m");
        String password = scanner.nextLine();

        Pair<Boolean, User> res = userTable.loginEmploy(email, password);
        if (res.getKey()) {
            Settings.setCurrentUser(res.getValue());


            System.out.println(GREEN + "Authentification avec succès." + RESET);
            // System.out.println(Settings.getCurrentUser());
        } else {
            System.out.println(RED + "Email ou mot de passe incorrect" + RESET);
        }
    }


}
