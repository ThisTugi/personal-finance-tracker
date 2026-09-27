package service;

import model.Transaction;
import model.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PortfolioServiceTest {

    private PortfolioService portfolioService;

    @BeforeEach
    public void setup() {
        portfolioService = new PortfolioService();
    }

    @Test
    public void testWeightedAverageCost_SingleBuy() {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("AAPL", TransactionType.BUY, 10, 150.0, LocalDate.now(), "Investment"));
        
        double avgCost = portfolioService.calculateWeightedAverageCost(transactions, "AAPL");
        assertEquals(150.0, avgCost, 0.01);
    }

    @Test
    public void testWeightedAverageCost_MultipleBuys() {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("AAPL", TransactionType.BUY, 10, 100.0, LocalDate.now(), "Investment"));
        transactions.add(new Transaction("AAPL", TransactionType.BUY, 10, 200.0, LocalDate.now(), "Investment"));
        
        double avgCost = portfolioService.calculateWeightedAverageCost(transactions, "AAPL");
        // (10*100 + 10*200) / 20 = 150.0
        assertEquals(150.0, avgCost, 0.01);
    }

    @Test
    public void testRealizedPnL_SellAll() {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("AAPL", TransactionType.BUY, 10, 100.0, LocalDate.now(), "Investment"));
        transactions.add(new Transaction("AAPL", TransactionType.SELL, 10, 150.0, LocalDate.now(), "Investment"));
        
        double pnl = portfolioService.calculateRealizedPnL(transactions, "AAPL");
        // 10 * (150 - 100) = 500.0
        assertEquals(500.0, pnl, 0.01);
    }

    @Test
    public void testRealizedPnL_PartialSellWithMultipleBuys() {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("AAPL", TransactionType.BUY, 10, 100.0, LocalDate.now(), "Investment"));
        transactions.add(new Transaction("AAPL", TransactionType.BUY, 10, 200.0, LocalDate.now(), "Investment"));
        transactions.add(new Transaction("AAPL", TransactionType.SELL, 10, 180.0, LocalDate.now(), "Investment"));
        
        double pnl = portfolioService.calculateRealizedPnL(transactions, "AAPL");
        // Average cost is 150. Selling 10 at 180.
        // Profit: (180 - 150) * 10 = 300.0
        assertEquals(300.0, pnl, 0.01);
    }
}
