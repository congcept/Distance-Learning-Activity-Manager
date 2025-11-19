package com.dlam.config;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/dlam_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "password"; // Team members: Change this to your local MySQL password
    public static Connection initializeDatabase() throws SQLException, ClassNotFoundException {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Include the dependency in pom.xml.");
            throw e;
        } catch (SQLException e) {
            System.err.println("Connection failed! Check output console");
            throw e;
        }
    }
}
