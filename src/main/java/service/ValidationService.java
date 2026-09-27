package service;

import model.Transaction;
import model.TransactionType;
import java.util.List;

public class ValidationService {

    public void validateTransaction(Transaction t, List<Transaction> allTransactions) throws IllegalArgumentException {
        if (t.getAmount() <= 0) {
            throw new IllegalArgumentException("Miktar 0'dan büyük olmalıdır.");
        }
        
        if (t.getPricePerUnit() < 0) {
            throw new IllegalArgumentException("Birim fiyat negatif olamaz.");
        }

        if (t.getAssetName() == null || t.getAssetName().trim().isEmpty()) {
            throw new IllegalArgumentException("Varlık adı boş olamaz.");
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
                throw new IllegalArgumentException("Mevcut sahip olunandan fazlası satılamaz. Sahip Olunan: " + currentAmount);
            }
        }
    }
}
