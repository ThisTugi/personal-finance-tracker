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

    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        return new Transaction(
                rs.getInt("id"),
                rs.getString("asset_name"),
                TransactionType.valueOf(rs.getString("type")),
                rs.getDouble("amount"),
                rs.getDouble("price_per_unit"),
                LocalDate.parse(rs.getString("date")),
                rs.getString("category")
        );
    }

    @Override
    public List<Transaction> getAll() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving transactions: " + e.getMessage());
        }
        return transactions;
    }

    @Override
    public List<Transaction> getByCategory(String category) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE category = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, category);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving transactions by category: " + e.getMessage());
        }
        return transactions;
    }

    @Override
    public Transaction getById(int id) {
        String sql = "SELECT * FROM transactions WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToTransaction(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving transaction by id: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void update(Transaction t) {
        String sql = "UPDATE transactions SET asset_name = ?, type = ?, amount = ?, price_per_unit = ?, date = ?, category = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, t.getAssetName());
            pstmt.setString(2, t.getType().name());
            pstmt.setDouble(3, t.getAmount());
            pstmt.setDouble(4, t.getPricePerUnit());
            pstmt.setString(5, t.getDate().toString());
            pstmt.setString(6, t.getCategory());
            pstmt.setInt(7, t.getId());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating transaction: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM transactions WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting transaction: " + e.getMessage());
        }
    }
}
