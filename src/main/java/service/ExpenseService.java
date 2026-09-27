package service;

import model.Transaction;
import model.TransactionType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseService {

    public double calculateTotalMonthlyExpense(List<Transaction> transactions, int year, int month) {
        return transactions.stream()
                .filter(t -> t.getType() == TransactionType.GIDER)
                .filter(t -> t.getDate().getYear() == year && t.getDate().getMonthValue() == month)
                .mapToDouble(Transaction::getTotalPrice)
                .sum();
    }

    public Map<String, Double> calculateExpenseDistribution(List<Transaction> transactions, int year, int month) {
        double totalExpense = calculateTotalMonthlyExpense(transactions, year, month);
        Map<String, Double> distribution = new HashMap<>();

        if (totalExpense == 0) {
            return distribution;
        }

        Map<String, Double> categoryTotals = new HashMap<>();
        for (Transaction t : transactions) {
            if (t.getType() == TransactionType.GIDER && t.getDate().getYear() == year && t.getDate().getMonthValue() == month) {
                categoryTotals.put(t.getCategory(), categoryTotals.getOrDefault(t.getCategory(), 0.0) + t.getTotalPrice());
            }
        }

        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            double percentage = (entry.getValue() / totalExpense) * 100.0;
            distribution.put(entry.getKey(), percentage);
        }

        return distribution;
    }
}
