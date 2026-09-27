package service;

import model.Transaction;
import model.TransactionType;

import java.util.List;

public class PortfolioService {

    public double calculateWeightedAverageCost(List<Transaction> transactions, String assetName) {
        double totalCost = 0.0;
        double totalAmount = 0.0;

        for (Transaction t : transactions) {
            if (t.getAssetName().equals(assetName) && t.getType() == TransactionType.BUY) {
                totalCost += (t.getAmount() * t.getPricePerUnit());
                totalAmount += t.getAmount();
            }
        }

        if (totalAmount == 0) {
            return 0.0;
        }
        return totalCost / totalAmount;
    }
}
