package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.StackPane;
import model.Transaction;
import repository.ITransactionRepository;
import repository.SqliteTransactionRepository;
import service.ExportService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class DashboardController {

    @FXML private Label lblTotalPortfolio;
    @FXML private Label lblMonthlyExpense;
    @FXML private Label lblNetPnL;
    
    @FXML private TableView<Transaction> transactionTable;
    @FXML private TableColumn<Transaction, Integer> colId;
    @FXML private TableColumn<Transaction, String> colAsset;
    @FXML private TableColumn<Transaction, String> colType;
    @FXML private TableColumn<Transaction, Double> colAmount;
    @FXML private TableColumn<Transaction, Double> colPrice;
    @FXML private TableColumn<Transaction, Double> colTotal;
    @FXML private TableColumn<Transaction, String> colCurrentPrice;
    @FXML private TableColumn<Transaction, String> colPnL;
    @FXML private TableColumn<Transaction, LocalDate> colDate;
    @FXML private TableColumn<Transaction, String> colCategory;
    
    @FXML private StackPane summaryChartContainer;
    @FXML private StackPane expenseChartContainer;
    @FXML private StackPane incomeChartContainer;

    private ITransactionRepository repository;
    private ObservableList<Transaction> transactionList;
    private service.MarketDataService marketDataService;

    public DashboardController() {
        repository = new SqliteTransactionRepository();
        marketDataService = new service.MarketDataService();
    }

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colAsset.setCellValueFactory(new PropertyValueFactory<>("assetName"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        
        colPrice.setCellValueFactory(new PropertyValueFactory<>("pricePerUnit"));
        colPrice.setCellFactory(column -> new TableCell<Transaction, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    Transaction t = getTableView().getItems().get(getIndex());
                    setText(String.format("%.2f %s", price, t.getCurrency()));
                }
            }
        });
        
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        colTotal.setCellFactory(column -> new TableCell<Transaction, Double>() {
            @Override
            protected void updateItem(Double total, boolean empty) {
                super.updateItem(total, empty);
                if (empty || total == null) {
                    setText(null);
                } else {
                    Transaction t = getTableView().getItems().get(getIndex());
                    setText(String.format("%.2f %s", total, t.getCurrency()));
                }
            }
        });
        
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));

        colCurrentPrice.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAssetName()));
        colCurrentPrice.setCellFactory(column -> new TableCell<Transaction, String>() {
            @Override
            protected void updateItem(String assetName, boolean empty) {
                super.updateItem(assetName, empty);
                setText(null);
                if (empty || assetName == null) return;
                Double currentPrice = marketDataService.getCurrentPrice(assetName);
                setText(currentPrice != null ? String.format("%.2f TL", currentPrice) : "-");
            }
        });

        colPnL.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAssetName()));
        colPnL.setCellFactory(column -> new TableCell<Transaction, String>() {
            @Override
            protected void updateItem(String assetName, boolean empty) {
                super.updateItem(assetName, empty);
                setText(null);
                setStyle("");
                if (empty || assetName == null) return;
                int idx = getIndex();
                if (idx < 0 || idx >= getTableView().getItems().size()) return;
                Transaction t = getTableView().getItems().get(idx);
                if (t == null || t.getType() != model.TransactionType.ALIM) { setText("-"); return; }
                Double currentPrice = marketDataService.getCurrentPrice(assetName);
                if (currentPrice == null) { setText("-"); return; }
                double pnl = (currentPrice - t.getPricePerUnit()) * t.getAmount();
                setText(String.format("%.2f TL", pnl));
                setStyle(pnl >= 0 ? "-fx-text-fill: #4caf50;" : "-fx-text-fill: #f44336;");
            }
        });

        // Verileri çekmeden önce piyasa verilerini alalım
        new Thread(() -> {
            marketDataService.fetchPrices();
            javafx.application.Platform.runLater(this::loadTransactions);
        }).start();
    }

    public void loadTransactions() {
        List<Transaction> dbTransactions = repository.getAll();
        transactionList = FXCollections.observableArrayList(dbTransactions);
        transactionTable.setItems(transactionList);
        updateSummaries(dbTransactions);
    }

    private void updateSummaries(List<Transaction> dbTransactions) {
        // Güncel portföy değeri: her varlık için (güncel fiyat × elde tutulan miktar)
        // Net pozisyon: ALIM miktarı - SATIM miktarı, sonra güncel fiyatla çarp
        Map<String, Double> netAmounts = new java.util.HashMap<>();
        for (Transaction t : dbTransactions) {
            if (t.getType() == model.TransactionType.ALIM || t.getType() == model.TransactionType.SATIM) {
                double delta = t.getType() == model.TransactionType.ALIM ? t.getAmount() : -t.getAmount();
                netAmounts.merge(t.getAssetName(), delta, Double::sum);
            }
        }
        double totalPortfolio = 0.0;
        for (Map.Entry<String, Double> entry : netAmounts.entrySet()) {
            if (entry.getValue() <= 0) continue;
            Double currentPrice = marketDataService.getCurrentPrice(entry.getKey());
            if (currentPrice != null) {
                totalPortfolio += currentPrice * entry.getValue();
            } else {
                // API'da fiyatı olmayan varlıklar için maliyet bazlı hesapla
                for (Transaction t : dbTransactions) {
                    if (t.getAssetName().equals(entry.getKey()) && t.getType() == model.TransactionType.ALIM) {
                        totalPortfolio += t.getTotalPrice();
                    }
                }
            }
        }

        service.ExpenseService expenseService = new service.ExpenseService();
        LocalDate now = LocalDate.now();
        double monthlyExpense = expenseService.calculateTotalMonthlyExpense(dbTransactions, now.getYear(), now.getMonthValue());

        lblTotalPortfolio.setText(String.format("%.2f TL", totalPortfolio));
        lblMonthlyExpense.setText(String.format("%.2f TL", monthlyExpense));
        service.PortfolioService portfolioService = new service.PortfolioService();
        double realizedPnL = portfolioService.calculateTotalRealizedPnL(dbTransactions);

        double unrealizedPnL = 0.0;
        for (Transaction t : dbTransactions) {
            if (t.getType() == model.TransactionType.ALIM) {
                Double currentPrice = marketDataService.getCurrentPrice(t.getAssetName());
                if (currentPrice != null) {
                    unrealizedPnL += (currentPrice - t.getPricePerUnit()) * t.getAmount();
                }
            }
        }
        double netPnL = realizedPnL + unrealizedPnL;

        lblNetPnL.setText(String.format("%.2f TL", netPnL));
        if (netPnL >= 0) {
            lblNetPnL.setStyle("-fx-text-fill: #4caf50;");
        } else {
            lblNetPnL.setStyle("-fx-text-fill: #f44336;");
        }
        java.util.Map<String, Double> expDistribution = expenseService.calculateExpenseDistribution(dbTransactions, now.getYear(), now.getMonthValue());
        javafx.scene.chart.PieChart expPieChart = new javafx.scene.chart.PieChart();
        for (java.util.Map.Entry<String, Double> entry : expDistribution.entrySet()) {
            expPieChart.getData().add(new javafx.scene.chart.PieChart.Data(entry.getKey(), entry.getValue()));
        }
        expenseChartContainer.getChildren().clear();
        expenseChartContainer.getChildren().add(expPieChart);
        java.util.Map<String, Double> incDistribution = expenseService.calculateIncomeDistribution(dbTransactions, now.getYear(), now.getMonthValue());
        javafx.scene.chart.PieChart incPieChart = new javafx.scene.chart.PieChart();
        for (java.util.Map.Entry<String, Double> entry : incDistribution.entrySet()) {
            incPieChart.getData().add(new javafx.scene.chart.PieChart.Data(entry.getKey(), entry.getValue()));
        }
        incomeChartContainer.getChildren().clear();
        incomeChartContainer.getChildren().add(incPieChart);
        java.util.Map<String, Double> assetDistribution = portfolioService.calculateAssetDistribution(dbTransactions);
        javafx.scene.chart.PieChart summaryPieChart = new javafx.scene.chart.PieChart();
        for (java.util.Map.Entry<String, Double> entry : assetDistribution.entrySet()) {
            summaryPieChart.getData().add(new javafx.scene.chart.PieChart.Data(entry.getKey(), entry.getValue()));
        }
        summaryChartContainer.getChildren().clear();
        summaryChartContainer.getChildren().add(summaryPieChart);
    }

    @FXML
    public void handleRefresh() {
        new Thread(() -> {
            marketDataService.fetchPrices();
            javafx.application.Platform.runLater(this::loadTransactions);
        }).start();
    }

    @FXML
    public void handleAddTransaction() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/ui/AddTransactionDialog.fxml"));
            javafx.scene.Parent root = loader.load();
            
            AddTransactionController controller = loader.getController();
            controller.setDashboardController(this);
            
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Yeni İşlem");
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.setScene(new javafx.scene.Scene(root));
            stage.showAndWait();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleDeleteTransaction() {
        Transaction selected = transactionTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Lütfen silmek için tablodan bir işlem seçin.", ButtonType.OK);
            alert.showAndWait();
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Seçili işlemi silmek istediğinize emin misiniz?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                repository.delete(selected.getId());
                loadTransactions();
            }
        });
    }

    @FXML
    public void handleExport() {
        ExportService exportService = new ExportService();
        exportService.exportTransactionsToCSV(repository.getAll(), "transactions_export.csv");
    }
}
