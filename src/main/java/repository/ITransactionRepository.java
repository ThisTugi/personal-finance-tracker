package repository;

import model.Transaction;
import java.util.List;

public interface ITransactionRepository {
    void add(Transaction t);
    List<Transaction> getAll();
    List<Transaction> getByCategory(String category);
    Transaction getById(int id);
    void update(Transaction t);
    void delete(int id);
}
