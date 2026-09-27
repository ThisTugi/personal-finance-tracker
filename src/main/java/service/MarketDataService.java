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
            
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject json = new JSONObject(response.body());
            
            for (String key : json.keySet()) {
                if (key.equals("Update_Date")) continue;
                JSONObject item = json.getJSONObject(key);
                if (item.has("Satış")) {
                    String satisStr = item.getString("Satış").replace(".", "").replace(",", ".").replace("$", "").trim();
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
