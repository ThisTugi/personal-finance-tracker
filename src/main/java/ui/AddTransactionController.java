package ui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Transaction;
import model.TransactionType;
import repository.ITransactionRepository;
import repository.SqliteTransactionRepository;
import service.ValidationService;

import java.time.LocalDate;

public class AddTransactionController {

    @FXML private TextField txtAsset;
    @FXML private ComboBox<TransactionType> cbType;
    @FXML private TextField txtAmount;
    @FXML private TextField txtPrice;
    @FXML private TextField txtCategory;
    @FXML private Label lblError;

    private ITransactionRepository repository;
    private ValidationService validationService;
    private DashboardController dashboardController;

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    @FXML
    public void initialize() {
        repository = new SqliteTransactionRepository();
        validationService = new ValidationService();
        cbType.setItems(FXCollections.observableArrayList(TransactionType.values()));
        cbType.getSelectionModel().selectFirst();
    }

    @FXML
    public void handleSave() {
        try {
            String asset = txtAsset.getText();
            TransactionType type = cbType.getValue();
            double amount = Double.parseDouble(txtAmount.getText());
            double price = Double.parseDouble(txtPrice.getText());
            String category = txtCategory.getText();

            Transaction t = new Transaction(asset, type, amount, price, LocalDate.now(), category);
            
            validationService.validateTransaction(t, repository.getAll());
            repository.add(t);
            
            if (dashboardController != null) {
                dashboardController.loadTransactions();
            }
            closeWindow();
        } catch (NumberFormatException e) {
            lblError.setText("Lütfen geçerli sayılar girin.");
        } catch (IllegalArgumentException e) {
            lblError.setText(e.getMessage());
        } catch (Exception e) {
            lblError.setText("Bir hata oluştu: " + e.getMessage());
        }
    }

    @FXML
    public void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) txtAsset.getScene().getWindow();
        stage.close();
    }
}
