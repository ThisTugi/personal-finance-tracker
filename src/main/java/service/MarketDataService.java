package service;

import org.json.JSONObject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class MarketDataService {
    private static final String TRUNCGIL_URL = "https://finans.truncgil.com/today.json";
    private static final String YAHOO_URL    = "https://query1.finance.yahoo.com/v8/finance/chart/%s.IS?interval=1d&range=1d";

    private final Map<String, String> ASSET_TO_API_KEY = new HashMap<>();
    private final Map<String, Double> currentPrices    = new HashMap<>();

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public MarketDataService() {
        ASSET_TO_API_KEY.put("Gram Alt\u0131n",       "gram-altin");
        ASSET_TO_API_KEY.put("\u00c7eyrek Alt\u0131n", "ceyrek-altin");
        ASSET_TO_API_KEY.put("Yar\u0131m Alt\u0131n",  "yarim-altin");
        ASSET_TO_API_KEY.put("Tam Alt\u0131n",         "tam-altin");
        ASSET_TO_API_KEY.put("G\u00fcm\u00fc\u015f",   "gumus");
        ASSET_TO_API_KEY.put("USD",                    "USD");
        ASSET_TO_API_KEY.put("EUR",                    "EUR");
    }

    public void fetchPrices() {
        fetchTruncgilPrices();
    }

    private void fetchTruncgilPrices() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(TRUNCGIL_URL))
                    .build();
            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            String body = new String(response.body(), java.nio.charset.StandardCharsets.UTF_8);
            JSONObject json = new JSONObject(body);

            currentPrices.clear();

            for (Map.Entry<String, String> entry : ASSET_TO_API_KEY.entrySet()) {
                String label  = entry.getKey();
                String apiKey = entry.getValue();
                if (!json.has(apiKey)) continue;
                JSONObject item = json.getJSONObject(apiKey);
                if (!item.has("Sat\u0131\u015F")) continue;
                String raw = item.getString("Sat\u0131\u015F")
                        .replace(".", "").replace(",", ".").replace("$", "").trim();
                try {
                    currentPrices.put(label, Double.parseDouble(raw));
                } catch (NumberFormatException ignored) {}
            }
            System.out.println("Alt\u0131n/D\u00f6viz fiyatlar\u0131 g\u00fcncellendi.");
        } catch (Exception e) {
            System.err.println("Truncgil API hatas\u0131: " + e.getMessage());
        }
    }

    public Double fetchStockPrice(String ticker) {
        try {
            String url = String.format(YAHOO_URL, ticker.toUpperCase());
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Mozilla/5.0")
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject json = new JSONObject(response.body());
            double price = json
                    .getJSONObject("chart")
                    .getJSONArray("result")
                    .getJSONObject(0)
                    .getJSONObject("meta")
                    .getDouble("regularMarketPrice");
            currentPrices.put(ticker.toUpperCase(), price);
            System.out.println(ticker.toUpperCase() + " fiyat\u0131: " + price + " TRY");
            return price;
        } catch (Exception e) {
            System.err.println(ticker + " hisse verisi al\u0131namad\u0131: " + e.getMessage());
            return null;
        }
    }

    public Double getCurrentPrice(String assetName) {
        if (assetName == null) return null;
        String key = assetName.trim();

        Double cached = currentPrices.get(key);
        if (cached != null) return cached;

        // Altın/döviz listesinde yoksa BIST hissesi olarak dene (Yahoo Finance .IS)
        if (!ASSET_TO_API_KEY.containsKey(key)) {
            return fetchStockPrice(key);
        }

        return null;
    }
}
