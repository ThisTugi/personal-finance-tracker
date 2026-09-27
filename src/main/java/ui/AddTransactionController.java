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

    @FXML private ComboBox<String> cbAsset;
    @FXML private Label lblAsset;
    @FXML private ComboBox<TransactionType> cbType;
    @FXML private TextField txtAmount;
    @FXML private Label lblAmount;
    @FXML private TextField txtPrice;
    @FXML private Label lblPrice;
    @FXML private ComboBox<String> cbCurrency;
    @FXML private ComboBox<String> cbCategory;
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
        cbCurrency.setItems(FXCollections.observableArrayList("TL", "USD", "EUR"));
        cbCurrency.getSelectionModel().selectFirst();
        
        cbType.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == TransactionType.GELIR) {
                lblAsset.setText("Açıklama / Kalem:");
                cbAsset.setItems(FXCollections.observableArrayList());
                lblAmount.setText("Adet (Oto: 1):");
                txtAmount.setText("1");
                txtAmount.setDisable(true);
                lblPrice.setText("Toplam Tutar:");
                cbCategory.setItems(FXCollections.observableArrayList(model.Category.getIncomeCategories()));
            } else if (newVal == TransactionType.GIDER) {
                lblAsset.setText("Açıklama / Kalem:");
                cbAsset.setItems(FXCollections.observableArrayList());
                lblAmount.setText("Adet (Oto: 1):");
                txtAmount.setText("1");
                txtAmount.setDisable(true);
                lblPrice.setText("Toplam Tutar:");
                cbCategory.setItems(FXCollections.observableArrayList(model.Category.getExpenseCategories()));
            } else {
                lblAsset.setText("Varlık Adı:");
                cbAsset.setItems(FXCollections.observableArrayList(
                    "Gram Altın", "Çeyrek Altın", "Yarım Altın", "Tam Altın", "Gümüş",
                    "USD", "EUR",
                    "THYAO", "ASELS", "GARAN", "YKBNK", "SISE", "KCHOL",
                    "BIMAS", "TUPRS", "AKBNK", "SAHOL", "EREGL", "TOASO"
                ));
                lblAmount.setText("Miktar:");
                txtAmount.setText("");
                txtAmount.setDisable(false);
                lblPrice.setText("Birim Fiyat:");
                cbCategory.setItems(FXCollections.observableArrayList(model.Category.getInvestmentCategories()));
            }
            cbCategory.getSelectionModel().selectFirst();
        });
        
        cbType.getSelectionModel().selectFirst();
    }

    @FXML
    public void handleSave() {
        try {
            String asset = cbAsset.getValue();
            if (asset == null || asset.trim().isEmpty()) {
                asset = cbAsset.getEditor().getText(); // Fallback for manual typing if getValue() returns null
            }
            TransactionType type = cbType.getValue();
            double amount = Double.parseDouble(txtAmount.getText());
            double price = Double.parseDouble(txtPrice.getText());
            String category = cbCategory.getValue();
            String currency = cbCurrency.getValue();

            Transaction t = new Transaction(asset, type, amount, price, LocalDate.now(), category, currency);
            
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
        Stage stage = (Stage) cbAsset.getScene().getWindow();
        stage.close();
    }
}
