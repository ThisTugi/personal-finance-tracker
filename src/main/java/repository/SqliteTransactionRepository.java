package repository;

import model.Transaction;
import model.TransactionType;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SqliteTransactionRepository implements ITransactionRepository {
    
    @Override
    public void add(Transaction t) {
        String sql = "INSERT INTO transactions (asset_name, type, amount, price_per_unit, date, category) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, t.getAssetName());
            pstmt.setString(2, t.getType().name());
            pstmt.setDouble(3, t.getAmount());
            pstmt.setDouble(4, t.getPricePerUnit());
            pstmt.setString(5, t.getDate().toString());
            pstmt.setString(6, t.getCategory());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding transaction: " + e.getMessage());
        }
    }

    @Override
    public List<Transaction> getAll() {
        return new ArrayList<>(); // To be implemented on Day 6
    }

    @Override
    public List<Transaction> getByCategory(String category) {
        return new ArrayList<>(); // To be implemented on Day 6
    }

    @Override
    public Transaction getById(int id) {
        return null; // To be implemented on Day 6
    }

    @Override
    public void update(Transaction t) {
        // To be implemented on Day 7
    }

    @Override
    public void delete(int id) {
        // To be implemented on Day 7
    }
}
