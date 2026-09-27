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
                            currentPrices.put("altın", price);
                            currentPrices.put("altin", price);
                            currentPrices.put("gram altın", price);
                            currentPrices.put("gram", price);
                        } else if (key.equals("USD")) {
                            currentPrices.put("dolar", price);
                            currentPrices.put("usd", price);
                        } else if (key.equals("EUR")) {
                            currentPrices.put("euro", price);
                            currentPrices.put("eur", price);
                        } else if (key.equals("gumus")) {
                            currentPrices.put("gümüş", price);
                        } else if (key.equals("ceyrek-altin")) {
                            currentPrices.put("çeyrek", price);
                            currentPrices.put("çeyrek altın", price);
                            currentPrices.put("ceyrek", price);
                        } else if (key.equals("yarim-altin")) {
                            currentPrices.put("yarım", price);
                            currentPrices.put("yarım altın", price);
                        } else if (key.equals("tam-altin")) {
                            currentPrices.put("tam", price);
                            currentPrices.put("tam altın", price);
                        }
                    } catch (NumberFormatException e) {
                        // ignore
                    }
                }
            }
            System.out.println("Market data fetched successfully. Cached " + currentPrices.size() + " assets.");
        } catch (Exception e) {
            System.err.println("Could not fetch market data: " + e.getMessage());
        }
    }

    public Double getCurrentPrice(String assetName) {
        if (assetName == null) return null;
        return currentPrices.get(assetName.toLowerCase().trim());
    }
}
