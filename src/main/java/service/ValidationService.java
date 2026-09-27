package service;

import model.Transaction;
import model.TransactionType;
import java.util.List;

public class ValidationService {

    public void validateTransaction(Transaction t, List<Transaction> allTransactions) throws IllegalArgumentException {
        if (t.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        
        if (t.getPricePerUnit() < 0) {
            throw new IllegalArgumentException("Price per unit cannot be negative");
        }

        if (t.getAssetName() == null || t.getAssetName().trim().isEmpty()) {
            throw new IllegalArgumentException("Asset name cannot be empty");
        }
        
        if (t.getType() == TransactionType.SATIM) {
            double currentAmount = 0.0;
            for (Transaction existing : allTransactions) {
                if (existing.getAssetName().equals(t.getAssetName())) {
                    if (existing.getType() == TransactionType.ALIM) {
                        currentAmount += existing.getAmount();
                    } else if (existing.getType() == TransactionType.SATIM) {
                        currentAmount -= existing.getAmount();
                    }
                }
            }
            if (t.getAmount() > currentAmount) {
                throw new IllegalArgumentException("Cannot sell more than currently owned. Owned: " + currentAmount);
            }
        }
    }
}
