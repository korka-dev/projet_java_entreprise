package org.post.dao;

import org.post.database.Connexion;
import org.post.models.User;
import org.post.utils.Hashing;
import org.post.utils.Pair;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private final String tableName;
    private final Connection connection;


    public UserDAO(String tableName) throws SQLException {
        this.tableName = tableName;
        try {
            this.connection = Connexion.getConnexion();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (!tableExists()) createTableSQL();

    }

    public boolean tableExists() throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        ResultSet resultSet = meta.getTables(null, null, tableName, new String[]{"TABLE"});

        return resultSet.next();
    }

    public void createTableSQL() {
        String tablesql = "CREATE TABLE " + this.tableName +
                "(" + "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                "nomComplet VARCHAR(255) NOT NULL," +
                "email VARCHAR(255) UNIQUE NOT NULL," +
                "password VARCHAR(255) NOT NULL," +
                "age INT," +
                "service DATE," +
                "salaire DECIMAL(10,2)," +
                "poste VARCHAR(255) NOT NULL)";


        Statement statement;
        try {
            statement = connection.createStatement();
            statement.executeUpdate(tablesql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Boolean insertEmploy(String nomComplet, String email, String password,int age, Date service, double salaire, String poste) throws SQLException {
        String insertSQL = "INSERT INTO employers(nomComplet,email,password,age,service,salaire,poste) VALUES (?,?,?,?,?,?,?)";

        // Création d'un objet PreparedStatement pour l'insertion
        PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
        preparedStatement.setString(1, nomComplet);
        preparedStatement.setString(2, email);
        preparedStatement.setString(3, Hashing.hashPassword(password));
        preparedStatement.setInt(4, age);
        preparedStatement.setDate(5, service);
        preparedStatement.setDouble(6, salaire);
        preparedStatement.setString(7, poste);

        // Exécutez la requête d'insertion
        int rowsInserted = preparedStatement.executeUpdate();
        preparedStatement.close();

        return rowsInserted > 0;
    }

    public User getByEmail(String email) throws SQLException {
        PreparedStatement preparedStatement;
        String sql = "SELECT * FROM employers WHERE email = ?";
        preparedStatement = this.connection.prepareStatement(sql);
        preparedStatement.setString(1, email);

        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            int id = resultSet.getInt("id");
            String nomComplet = resultSet.getString("nomComplet");
            int age = resultSet.getInt("age");
            Date service = resultSet.getDate("service");
            double salaire = resultSet.getDouble("salaire");
            String poste = resultSet.getString("poste");

            return new User(id, nomComplet, email,age,service,salaire,poste);
        } else {
            return null;
        }
    }

    public List<User> findByStatement(PreparedStatement preparedStatement) throws SQLException {
        List<User> users = new ArrayList<>();

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String nomComplet = resultSet.getString("nomComplet");
            String email = resultSet.getString("email");
            int age = resultSet.getInt("age");
            Date service = resultSet.getDate("service");
            double salaire = resultSet.getDouble("salaire");
            String poste = resultSet.getString("poste");

            User user = new User(id, nomComplet, email, age, service, salaire, poste);
            users.add(user);
        }

        resultSet.close();
        preparedStatement.close();

        return users;
    }

    public List<User> getAllEmployes() throws SQLException {

        PreparedStatement preparedStatement;

        String sql = "SELECT * FROM employers";
        preparedStatement = this.connection.prepareStatement(sql);

        return findByStatement(preparedStatement);
    }


    public Pair<Boolean, User> loginEmploy(String email, String password) throws SQLException {
        String sql = "SELECT id, nomComplet, password, age, service, salaire, poste FROM employers WHERE email = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, email);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            int id = resultSet.getInt("id");
            String nomComplet = resultSet.getString("nomComplet");
            String hashedPasswordFromDB = resultSet.getString("password");
            int age = resultSet.getInt("age");
            Date service = resultSet.getDate("service");
            double salaire = resultSet.getDouble("salaire");
            String poste = resultSet.getString("poste");

            User user = new User(id, nomComplet, email, age, service, salaire, poste);

            // Vérifier le mot de passe et si le compte est actif
            Boolean verified = Hashing.verifyPassword(password, hashedPasswordFromDB);
            return new Pair<>(verified, user);
        }

        // Aucun utilisateur trouvé avec cet email
        return new Pair<>(false, new User());
    }

    public List<User> getEmployeesByPost(String poste) throws SQLException {
        List<User> employees = new ArrayList<>();

        String selectSQL = "SELECT * FROM employers WHERE poste = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {
            preparedStatement.setString(1, poste);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nomComplet = resultSet.getString("nomComplet");
                String email = resultSet.getString("email");
                int age = resultSet.getInt("age");
                Date service = resultSet.getDate("service");
                double salaire = resultSet.getDouble("salaire");
                String userPoste = resultSet.getString("poste");

                User employee = new User(id, nomComplet, email, age, service, salaire, userPoste);
                employees.add(employee);
            }
        }

        return employees;
    }

    public void displaySalariesByPost(String poste) throws SQLException {
        String selectSQL = "SELECT nomComplet, salaire FROM employers WHERE poste = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {
            preparedStatement.setString(1, poste);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.isBeforeFirst()) {
                System.out.println("\u001B[31mAucun employé trouvé avec le poste de " + poste + "\u001B[0m");
                return;
            }

            System.out.println("\u001B[36mListe des salaires pour le poste de " + poste + "\u001B[0m");
            while (resultSet.next()) {
                String nomComplet = resultSet.getString("nomComplet");
                double salaire = resultSet.getDouble("salaire");

                System.out.println("\u001B[32m" + nomComplet + " : " + salaire + "\u001B[0m");
            }
        }
    }

    public  void displayEmployeeInfoByEmail(String email) throws SQLException {
        User employee = getByEmail(email);

        if (employee != null) {
            System.out.println("\nInformations de l'employé");
            System.out.println("\u001B[36mNom complet : \u001B[0m" + employee.getNomComplet());
            System.out.println("\u001B[36mEmail : \u001B[0m" + employee.getEmail());
            System.out.println("\u001B[36mAge : \u001B[0m" + employee.getAge());
            System.out.println("\u001B[36mDate d'entrée en service : \u001B[0m" + employee.getService());
            System.out.println("\u001B[36mSalaire : \u001B[0m" + employee.getSalaire());
            System.out.println("\u001B[36mPoste : \u001B[0m" + employee.getPoste());
        } else {
            System.out.println("\u001B[31mAucun employé trouvé avec cet email.\u001B[0m");
        }
    }

    public double calculateAverageSalary() throws SQLException {
        List<User> employees = getAllEmployes();

        if (employees.isEmpty()) {

            System.out.println("\u001B[31mAucun employé trouvé.\u001B[0m");
            return 0.0;
        }

        double totalSalary = 0;

        for (User employee : employees) {
            totalSalary += employee.getSalaire();
        }

        return totalSalary / employees.size();
    }


    public int getTotalEmployees() throws SQLException {
        String query;
        query = "SELECT COUNT(*) FROM employers";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        }
        return 0;
    }







}
