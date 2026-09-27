package service;

import model.Transaction;
import model.TransactionType;

import java.util.List;

public class PortfolioService {

    public double calculateWeightedAverageCost(List<Transaction> transactions, String assetName) {
        double totalCost = 0.0;
        double totalAmount = 0.0;

        for (Transaction t : transactions) {
            if (t.getAssetName().equals(assetName) && t.getType() == TransactionType.ALIM) {
                totalCost += (t.getAmount() * t.getPricePerUnit());
                totalAmount += t.getAmount();
            }
        }

        if (totalAmount == 0) {
            return 0.0;
        }
        return totalCost / totalAmount;
    }

    public double calculateRealizedPnL(List<Transaction> transactions, String assetName) {
        double realizedPnL = 0.0;
        double currentAmount = 0.0;
        double totalCost = 0.0;

        for (Transaction t : transactions) {
            if (t.getAssetName().equals(assetName)) {
                if (t.getType() == TransactionType.ALIM) {
                    currentAmount += t.getAmount();
                    totalCost += (t.getAmount() * t.getPricePerUnit());
                } else if (t.getType() == TransactionType.SATIM) {
                    double averageCost = (currentAmount == 0) ? 0 : (totalCost / currentAmount);
                    double profit = (t.getPricePerUnit() - averageCost) * t.getAmount();
                    realizedPnL += profit;
                    
                    currentAmount -= t.getAmount();
                    totalCost -= (averageCost * t.getAmount());
                }
            }
        }
        return realizedPnL;
    }
    public double calculateTotalRealizedPnL(List<Transaction> transactions) {
        return transactions.stream()
                .map(Transaction::getAssetName)
                .distinct()
                .mapToDouble(asset -> calculateRealizedPnL(transactions, asset))
                .sum();
    }

    public java.util.Map<String, Double> calculateAssetDistribution(List<Transaction> transactions) {
        java.util.Map<String, Double> distribution = new java.util.HashMap<>();
        
        double cash = 0;
        for (Transaction t : transactions) {
            if (t.getType() == TransactionType.GELIR || t.getType() == TransactionType.SATIM) {
                cash += t.getTotalPrice();
            } else if (t.getType() == TransactionType.GIDER || t.getType() == TransactionType.ALIM) {
                cash -= t.getTotalPrice();
            }
        }
        if (cash > 0) {
            distribution.put("Nakit", cash);
        }

        List<String> assets = transactions.stream()
                .filter(t -> t.getType() == TransactionType.ALIM || t.getType() == TransactionType.SATIM)
                .map(Transaction::getAssetName)
                .distinct()
                .toList();

        for (String asset : assets) {
            double currentAmount = 0.0;
            for (Transaction t : transactions) {
                if (t.getAssetName().equals(asset)) {
                    if (t.getType() == TransactionType.ALIM) currentAmount += t.getAmount();
                    else if (t.getType() == TransactionType.SATIM) currentAmount -= t.getAmount();
                }
            }
            if (currentAmount > 0) {
                double avgCost = calculateWeightedAverageCost(transactions, asset);
                distribution.put(asset, currentAmount * avgCost);
            }
        }
        
        return distribution;
    }
}
