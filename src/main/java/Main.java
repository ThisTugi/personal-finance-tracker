import model.Transaction;
import model.TransactionType;
import repository.DatabaseManager;
import repository.ITransactionRepository;
import repository.SqliteTransactionRepository;

import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Uygulama başlarken tabloları otomatik oluşturur/kontrol eder
        DatabaseManager.initializeDatabase();
        
        ITransactionRepository repo = new SqliteTransactionRepository();
        
        // Veri ekleme
        System.out.println("Adding test data...");
        repo.add(new Transaction("AAPL", TransactionType.BUY, 10, 150.0, LocalDate.now(), "Investment"));
        repo.add(new Transaction("Grocery", TransactionType.EXPENSE, 1, 50.0, LocalDate.now(), "Food"));
        repo.add(new Transaction("Salary", TransactionType.INCOME, 1, 3000.0, LocalDate.now(), "Salary"));
        
        // Veri çekme ve yazdırma
        System.out.println("All transactions:");
        List<Transaction> transactions = repo.getAll();
        for (Transaction t : transactions) {
            System.out.println(t);
        }
        
        // Güncelleme test
        if (!transactions.isEmpty()) {
            Transaction first = transactions.get(0);
            first.setAmount(20);
            repo.update(first);
            System.out.println("Updated transaction: " + repo.getById(first.getId()));
            
            // Silme test
            repo.delete(first.getId());
            System.out.println("Deleted transaction. Remaining count: " + repo.getAll().size());
        }
    }
}