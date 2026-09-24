package model;
import java.time.LocalDate;


public class Transaction {
    private int id;
    private String assetName;
    private TransactionType type;
    private double amount;
    private double pricePerUnit;
    private LocalDate date;
    private String category;

    public Transaction(String assetName, TransactionType type, double amount, double pricePerUnit, LocalDate date, String category) {
        this.assetName = assetName;
        this.type = type;
        this.amount = amount;
        this.pricePerUnit = pricePerUnit;
        this.date = date;
        this.category = category;
    }

    public Transaction(int id, String assetName, TransactionType type, double amount, double pricePerUnit, LocalDate date, String category) {
        this.id = id;
        this.assetName = assetName;
        this.type = type;
        this.amount = amount;
        this.pricePerUnit = pricePerUnit;
        this.date = date;
        this.category = category;
    }

    public double getTotalPrice() {
        return this.amount * this.pricePerUnit;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", assetName='" + assetName + '\'' +
                ", type=" + type +
                ", amount=" + amount +
                ", pricePerUnit=" + pricePerUnit +
                ", totalPrice=" + getTotalPrice() +
                ", date=" + date +
                ", category='" + category + '\'' +
                '}';
    }
}
