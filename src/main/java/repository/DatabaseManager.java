package repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:finance.db";

    private DatabaseManager() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        String sql = """
            CREATE TABLE IF NOT EXISTS transactions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                asset_name TEXT NOT NULL,
                type TEXT NOT NULL,
                amount REAL NOT NULL,
                price_per_unit REAL NOT NULL,
                date TEXT NOT NULL,
                category TEXT NOT NULL
            );
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            System.out.println("Veritabanı tabloları başarıyla hazırlandı.");

        } catch (SQLException e) {
            System.err.println("Veritabanı başlatılırken hata oluştu: " + e.getMessage());
        }
    }
}