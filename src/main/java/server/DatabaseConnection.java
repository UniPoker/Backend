package server;

import java.sql.*;

public class DatabaseConnection {

    private static final DatabaseConnection CLASS_INSTANCE = new DatabaseConnection();
    private Connection conn = null;

    /**
     * creates a connection to the H2 Database. Creates the table Users if not already existence.
     */
    private DatabaseConnection() {
        try {
            Class.forName("org.h2.Driver");
            conn = DriverManager.getConnection("jdbc:h2:Database", "", "");
            Statement stmt = conn.createStatement();
            String create = "CREATE TABLE IF NOT EXISTS Users (ID INT PRIMARY KEY AUTO_INCREMENT NOT NULL, name VARCHAR(255) UNIQUE, password VARCHAR(255));";
            stmt.executeUpdate(create);
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * returns the class instance of the DatabaseConnection so only one instance is active all the time
     *
     * @return returns the instance of DatabaseConnection
     */
    public static DatabaseConnection getInstance() {
        return CLASS_INSTANCE;
    }

    /**
     * Searches for an User with the given id in the Database and returns the ResultSet
     *
     * @param id the id of the user to search for
     * @return returns the ResultSet of the Query
     */
    public ResultSet getUserById(int id) {
        try {
            String sql = "SELECT * FROM Users WHERE ID = ?;";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Searches for an User with the given name in the Database and returns the ResultSet
     *
     * @param name the name of the user to search for
     * @return returns the ResultSet of the Query
     */
    public ResultSet getUserByName(String name) {
        try {
            String sql = "SELECT * FROM Users WHERE name = ?;";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * inserts a new User in the Database with the given name and password.
     *
     * @param username the username of the new user
     * @param password the password of the new user
     * @return returns the ResultSet of the Query. Is -1 if there is an SQLExceptions
     */
    public int insertUser(String username, String password) {
        try {
            String sql = "INSERT INTO Users(name, password) VALUES (?,?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * gets the number of Users in the Database
     *
     * @return the number of Users in the Database
     * @throws SQLException if there are any SQL errors. Should not happen
     */
    public int countUsers() throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT COUNT(Id)AS count FROM Users ");
        int count = 0;
        while (rs.next()) {
            count = rs.getInt("count");
        }
        return count;
    }

    /**
     * closes the connection to the Database
     *
     * @throws SQLException if there are any errors
     */
    public void closeConnection() throws SQLException {
        conn.close();
    }
}