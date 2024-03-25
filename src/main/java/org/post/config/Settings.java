package org.post.config;

import org.post.models.User;

public class Settings {

    private final String mysqlUser;
    private  String mysqlPassword;
    private  String mysqlHost;
    private  String mysqlPort;
    private  String mysqlDatabase;

    private static org.post.models.User currentUser;
    public static String cacheFileName = "current_user.ser";

    public Settings() {
        mysqlUser = System.getenv("MYSQL_USER");
        mysqlPassword = System.getenv("MYSQL_PASSWORD");
        mysqlHost = System.getenv("MYSQL_HOST");
        mysqlPort = System.getenv("MYSQL_PORT");
        mysqlDatabase = System.getenv("MYSQL_DATABASE");
    }

    public String getUrl() {
        return String.format("jdbc:mysql://%s:%s/%s", mysqlHost, mysqlPort, mysqlDatabase);
    }

    public String getUser() {
        return mysqlUser;
    }

    public static User getCurrentUser() {
        if (currentUser == null)
            throw new RuntimeException("Aucun utilisateur authentifié. Pensez à vous connecter pour obtenir l'accès");
        return currentUser;
    }

    public static void setCurrentUser(org.post.models.User newUser) {
        currentUser = newUser;
    }


    public String getPassword() {
        return mysqlPassword;
    }
}
