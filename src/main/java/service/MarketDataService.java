package service;

import org.json.JSONObject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class MarketDataService {
    private static final String API_URL = "https://finans.truncgil.com/today.json";
    private Map<String, Double> currentPrices = new HashMap<>();

    public void fetchPrices() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .build();
            
            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
            String body = new String(response.body(), java.nio.charset.StandardCharsets.UTF_8);
            JSONObject json = new JSONObject(body);
            
            for (String key : json.keySet()) {
                if (key.equals("Update_Date")) continue;
                JSONObject item = json.getJSONObject(key);
                if (item.has("Sat\u0131\u015F")) { // Satış with unicode escape
                    String satisStr = item.getString("Sat\u0131\u015F").replace(".", "").replace(",", ".").replace("$", "").trim();
                    try {
                        double price = Double.parseDouble(satisStr);
                        currentPrices.put(key.toLowerCase(), price);
                        
                        if (key.equals("gram-altin")) {
                            currentPrices.put("gram alt\u0131n", price);
                            currentPrices.put("alt\u0131n", price);
                            currentPrices.put("altin", price);
                            currentPrices.put("gram", price);
                        } else if (key.equals("USD")) {
                            currentPrices.put("dolar", price);
                            currentPrices.put("usd", price);
                        } else if (key.equals("EUR")) {
                            currentPrices.put("euro", price);
                            currentPrices.put("eur", price);
                        } else if (key.equals("gumus")) {
                            currentPrices.put("g\u00fcm\u00fc\u015f", price);
                        } else if (key.equals("ceyrek-altin")) {
                            currentPrices.put("\u00e7eyrek", price);
                            currentPrices.put("\u00e7eyrek alt\u0131n", price);
                            currentPrices.put("ceyrek", price);
                        } else if (key.equals("yarim-altin")) {
                            currentPrices.put("yar\u0131m", price);
                            currentPrices.put("yar\u0131m alt\u0131n", price);
                        } else if (key.equals("tam-altin")) {
                            currentPrices.put("tam", price);
                            currentPrices.put("tam alt\u0131n", price);
                        }
                    } catch (NumberFormatException e) {
                    }
                }
            }
            System.out.println("Market data fetched successfully. Cached " + currentPrices.size() + " assets.");
            System.out.println("gram alt\u0131n price: " + currentPrices.get("gram alt\u0131n"));
            System.out.println("\u00e7eyrek alt\u0131n price: " + currentPrices.get("\u00e7eyrek alt\u0131n"));
        } catch (Exception e) {
            System.err.println("Could not fetch market data: " + e.getMessage());
        }
    }

    public Double getCurrentPrice(String assetName) {
        if (assetName == null) return null;
        String normalized = assetName.trim().toLowerCase(java.util.Locale.forLanguageTag("tr-TR"));
        Double price = currentPrices.get(normalized);
        if (price == null) {
            price = currentPrices.get(assetName.trim().toLowerCase(java.util.Locale.ENGLISH));
        }
        return price;
    }
}
