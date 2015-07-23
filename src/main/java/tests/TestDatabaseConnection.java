package tests;

import java.sql.*;

public class TestDatabaseConnection {

    private static final TestDatabaseConnection CLASS_INSTANCE = new TestDatabaseConnection();
    private Connection conn = null;

    private TestDatabaseConnection() {
        try {
            Class.forName("org.h2.Driver");
            conn = DriverManager.getConnection("jdbc:h2:Test", "", "");
            Statement stmt = conn.createStatement();
            String create = "CREATE TABLE IF NOT EXISTS Users (ID INT PRIMARY KEY AUTO_INCREMENT NOT NULL, name VARCHAR(255) UNIQUE, password VARCHAR(255));";
            stmt.executeUpdate(create);
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    public static TestDatabaseConnection getInstance() {
        return CLASS_INSTANCE;
    }

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

    public int countUsers() throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT COUNT(Id)AS count FROM Users ");
        int count = 0;
        while(rs.next()){
            count = rs.getInt("count");
        }
        return count;
    }

    public void closeConnection() throws SQLException {
        conn.close();
    }
}