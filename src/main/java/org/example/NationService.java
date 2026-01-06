package org.example;

import com.google.gson.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class NationService {

    private final List<Nation> nations = new ArrayList<>();

    public NationService(String apiKey) {
        loadNations(apiKey);
    }

    private void loadNations(String apiKey) {
        try {
            String url = "http://api.countrylayer.com/v2/all?access_key=" + apiKey;

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            parseResponse(response.body());

            System.out.println("✅ Loaded " + nations.size() + " nations");
        } catch (Exception e) {
            throw new RuntimeException("Failed to load nations", e);
        }
    }

    private void parseResponse(String json) {
        JsonArray array = JsonParser.parseString(json).getAsJsonArray();
        for (JsonElement element : array) {
            JsonObject obj = element.getAsJsonObject();

            String name = safeString(obj, "name");
            String capital = safeString(obj, "capital");
            String region = safeString(obj, "region");

            List<String> tlds = new ArrayList<>();
            JsonArray tldsArray = obj.getAsJsonArray("topLevelDomain");
            if (tldsArray != null) {
                for (JsonElement t : tldsArray) tlds.add(t.getAsString());
            }

            nations.add(new Nation(name, capital, region, tlds));
        }
    }

    private String safeString(JsonObject obj, String field) {
        JsonElement el = obj.get(field);
        return (el == null || el.isJsonNull()) ? "Unknown" : el.getAsString();
    }

    public List<Nation> getAllNations() { return nations; }
    public Nation getRandomNation() { return nations.get((int) (Math.random() * nations.size())); }
}
