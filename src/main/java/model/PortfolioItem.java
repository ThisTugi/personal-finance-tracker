package model;

public class PortfolioItem {
    private String assetName;
    private double totalQuantity;
    private double averageCost;
    private double currentPrice;

    public PortfolioItem(String assetName, double totalQuantity, double averageCost, double currentPrice) {
        this.assetName = assetName;
        this.totalQuantity = totalQuantity;
        this.averageCost = averageCost;
        this.currentPrice = currentPrice;
    }

    public double getTotalCost() {
        return totalQuantity * averageCost;
    }

    public double getCurrentValue() {
        return totalQuantity * currentPrice;
    }

    public double getProfitOrLoss() {
        return getCurrentValue() - getTotalCost();
    }

    public double getProfitOrLossPercentage() {
        if (averageCost == 0) {
            return 0.0;
        }
        return ((currentPrice - averageCost) / averageCost) * 100.0;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public double getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(double totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public double getAverageCost() {
        return averageCost;
    }

    public void setAverageCost(double averageCost) {
        this.averageCost = averageCost;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    @Override
    public String toString() {
        return String.format(
                "%s | Adet: %.2f | Ort. Maliyet: %.2f | Güncel: %.2f | Değer: %.2f | PnL: %.2f (%%%.2f)",
                assetName, totalQuantity, averageCost, currentPrice, getCurrentValue(), getProfitOrLoss(), getProfitOrLossPercentage()
        );
    }
}
