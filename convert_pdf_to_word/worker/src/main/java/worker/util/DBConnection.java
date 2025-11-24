package worker.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // Get from environment variables or use defaults
    private static final String DB_HOST = System.getenv().getOrDefault("DB_HOST", "localhost");
    private static final String DB_PORT = System.getenv().getOrDefault("DB_PORT", "3306");
    private static final String DB_NAME = System.getenv().getOrDefault("DB_NAME", "pdf_converter");
    private static final String DB_USER = System.getenv().getOrDefault("DB_USER", "root");
    private static final String DB_PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "");
    
    private static final String URL = String.format(
        "jdbc:mysql://%s:%s/%s?useSSL=false&useUnicode=true&characterEncoding=UTF-8",
        DB_HOST, DB_PORT, DB_NAME
    );

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("[WORKER] Database connection configured:");
            System.out.println("  Host: " + DB_HOST);
            System.out.println("  Database: " + DB_NAME);
        } catch (ClassNotFoundException e) {
            System.err.println("[WORKER] MySQL driver not found!");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
    }
}

