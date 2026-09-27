package service;

import model.Transaction;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ExportService {

    public void exportTransactionsToCSV(List<Transaction> transactions, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.append("ID,Asset Name,Type,Amount,Price Per Unit,Total Price,Date,Category\n");
            
            for (Transaction t : transactions) {
                writer.append(String.valueOf(t.getId())).append(",");
                writer.append(t.getAssetName()).append(",");
                writer.append(t.getType().name()).append(",");
                writer.append(String.valueOf(t.getAmount())).append(",");
                writer.append(String.valueOf(t.getPricePerUnit())).append(",");
                writer.append(String.valueOf(t.getTotalPrice())).append(",");
                writer.append(t.getDate().toString()).append(",");
                writer.append(t.getCategory()).append("\n");
            }
            System.out.println("Data exported to " + filePath + " successfully.");
        } catch (IOException e) {
            System.err.println("Error exporting to CSV: " + e.getMessage());
        }
    }
}
