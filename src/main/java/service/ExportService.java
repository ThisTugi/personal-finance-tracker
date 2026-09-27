package service;

import model.Transaction;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ExportService {

    public String exportTransactionsToCSV(List<Transaction> transactions, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))) {

            // UTF-8 BOM → Excel'in Türkçe karakterleri doğru okuyabilmesi için
            writer.write('\uFEFF');

            writer.write("ID,Varl\u0131k Ad\u0131,T\u00fcr,Adet,Birim Fiyat,Toplam Tutar,Para Birimi,Tarih,Kategori");
            writer.newLine();

            for (Transaction t : transactions) {
                writer.write(String.valueOf(t.getId()));
                writer.write(",");
                writer.write(escapeCsv(t.getAssetName()));
                writer.write(",");
                writer.write(escapeCsv(t.getType().name()));
                writer.write(",");
                writer.write(String.valueOf(t.getAmount()));
                writer.write(",");
                writer.write(String.format("%.2f", t.getPricePerUnit()));
                writer.write(",");
                writer.write(String.format("%.2f", t.getTotalPrice()));
                writer.write(",");
                writer.write(escapeCsv(t.getCurrency() != null ? t.getCurrency() : "TL"));
                writer.write(",");
                writer.write(t.getDate().toString());
                writer.write(",");
                writer.write(escapeCsv(t.getCategory() != null ? t.getCategory() : ""));
                writer.newLine();
            }

            System.out.println("CSV ba\u015far\u0131yla olu\u015fturuldu: " + filePath);
            return filePath;
        } catch (IOException e) {
            System.err.println("CSV hatas\u0131: " + e.getMessage());
            return null;
        }
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
