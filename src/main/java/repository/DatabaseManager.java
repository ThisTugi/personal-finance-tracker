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
                category TEXT NOT NULL,
                currency TEXT DEFAULT 'TL'
            );
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            
            stmt.executeUpdate("UPDATE transactions SET type = 'ALIM' WHERE type = 'BUY'");
            stmt.executeUpdate("UPDATE transactions SET type = 'SATIM' WHERE type = 'SELL'");
            stmt.executeUpdate("UPDATE transactions SET type = 'GIDER' WHERE type = 'EXPENSE'");
            stmt.executeUpdate("UPDATE transactions SET type = 'GELIR' WHERE type = 'INCOME'");
            
            try {
                stmt.execute("ALTER TABLE transactions ADD COLUMN currency TEXT DEFAULT 'TL'");
            } catch (SQLException ignore) {
            }
            
            System.out.println("Veritabanı tabloları başarıyla hazırlandı ve güncellendi.");

        } catch (SQLException e) {
            System.err.println("Veritabanı başlatılırken hata oluştu: " + e.getMessage());
        }
    }
}