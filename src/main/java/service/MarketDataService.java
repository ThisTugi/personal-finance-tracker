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

    // ComboBox'taki etiket -> API'daki JSON anahtarı
    private final Map<String, String> ASSET_TO_API_KEY = new HashMap<>();
    private final Map<String, Double> currentPrices = new HashMap<>();

    public MarketDataService() {
        ASSET_TO_API_KEY.put("Gram Alt\u0131n",        "gram-altin");
        ASSET_TO_API_KEY.put("\u00c7eyrek Alt\u0131n",  "ceyrek-altin");
        ASSET_TO_API_KEY.put("Yar\u0131m Alt\u0131n",   "yarim-altin");
        ASSET_TO_API_KEY.put("Tam Alt\u0131n",          "tam-altin");
        ASSET_TO_API_KEY.put("G\u00fcm\u00fc\u015f",    "gumus");
        ASSET_TO_API_KEY.put("USD",                     "USD");
        ASSET_TO_API_KEY.put("EUR",                     "EUR");
    }

    public void fetchPrices() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .build();

            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
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
                        .replace(".", "")
                        .replace(",", ".")
                        .replace("$", "")
                        .trim();
                try {
                    currentPrices.put(label, Double.parseDouble(raw));
                } catch (NumberFormatException ignored) {}
            }

            System.out.println("Piyasa verileri g\u00fcncellendi: " + currentPrices);
        } catch (Exception e) {
            System.err.println("API hatas\u0131: " + e.getMessage());
        }
    }

    public Double getCurrentPrice(String assetName) {
        if (assetName == null) return null;
        return currentPrices.get(assetName.trim());
    }
}
