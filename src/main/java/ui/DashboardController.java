package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import model.Transaction;
import repository.ITransactionRepository;
import repository.SqliteTransactionRepository;
import service.ExportService;

import java.time.LocalDate;
import java.util.List;

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
    @FXML private TableColumn<Transaction, LocalDate> colDate;
    @FXML private TableColumn<Transaction, String> colCategory;
    
    @FXML private StackPane chartContainer;

    private ITransactionRepository repository;
    private ObservableList<Transaction> transactionList;

    public DashboardController() {
        repository = new SqliteTransactionRepository();
    }

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colAsset.setCellValueFactory(new PropertyValueFactory<>("assetName"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("pricePerUnit"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));

        loadTransactions();
    }

    public void loadTransactions() {
        List<Transaction> dbTransactions = repository.getAll();
        transactionList = FXCollections.observableArrayList(dbTransactions);
        transactionTable.setItems(transactionList);
        updateSummaries(dbTransactions);
    }

    private void updateSummaries(List<Transaction> dbTransactions) {
        double totalPortfolio = dbTransactions.stream()
            .filter(t -> t.getType() == model.TransactionType.ALIM)
            .mapToDouble(Transaction::getTotalPrice).sum() 
            - dbTransactions.stream()
            .filter(t -> t.getType() == model.TransactionType.SATIM)
            .mapToDouble(Transaction::getTotalPrice).sum();

        service.ExpenseService expenseService = new service.ExpenseService();
        LocalDate now = LocalDate.now();
        double monthlyExpense = expenseService.calculateTotalMonthlyExpense(dbTransactions, now.getYear(), now.getMonthValue());

        lblTotalPortfolio.setText(String.format("%.2f TL", totalPortfolio));
        lblMonthlyExpense.setText(String.format("%.2f TL", monthlyExpense));
        
        // PnL placeholder logic
        lblNetPnL.setText("Hesaplanıyor...");

        java.util.Map<String, Double> distribution = expenseService.calculateExpenseDistribution(dbTransactions, now.getYear(), now.getMonthValue());
        javafx.scene.chart.PieChart pieChart = new javafx.scene.chart.PieChart();
        for (java.util.Map.Entry<String, Double> entry : distribution.entrySet()) {
            pieChart.getData().add(new javafx.scene.chart.PieChart.Data(entry.getKey(), entry.getValue()));
        }
        chartContainer.getChildren().clear();
        chartContainer.getChildren().add(pieChart);
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
    public void handleExport() {
        ExportService exportService = new ExportService();
        exportService.exportTransactionsToCSV(repository.getAll(), "transactions_export.csv");
    }
}
