package org.example;

import com.google.gson.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class NationService {

    private final List<Nation> nations = new ArrayList<>();

    public NationService() {
        loadNations();
    }

    private void loadNations() {
        try {
            String url = "https://restcountries.com/v3.1/all?fields=name,capital,region,tld,flags,currencies,languages";

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

            String name = safeNestedString(obj, "name", "common");
            String capital = safeArrayFirst(obj, "capital");
            String region = safeString(obj, "region");
            String flagUrl = safeNestedString(obj, "flags", "svg");

            // Top level domain
            List<String> tlds = new ArrayList<>();
            JsonArray tldsArray = obj.getAsJsonArray("tld");
            if (tldsArray != null) {
                for (JsonElement t : tldsArray) tlds.add(t.getAsString());
            }

            // Currencies
            Map<String, String> currencies = new HashMap<>();
            JsonObject currencyObj = obj.getAsJsonObject("currencies");
            if (currencyObj != null) {
                for (String key : currencyObj.keySet()) {
                    JsonObject cur = currencyObj.getAsJsonObject(key);
                    if (cur != null && cur.has("name")) {
                        currencies.put(key, cur.get("name").getAsString());
                    }
                }
            }

            // Languages
            Map<String, String> languages = new HashMap<>();
            JsonObject langObj = obj.getAsJsonObject("languages");
            if (langObj != null) {
                for (String key : langObj.keySet()) {
                    languages.put(key, langObj.get(key).getAsString());
                }
            }

            nations.add(new Nation(name, capital, region, tlds, flagUrl, currencies, languages));
        }
    }

    private String safeString(JsonObject obj, String field) {
        JsonElement el = obj.get(field);
        return (el == null || el.isJsonNull()) ? "Unknown" : el.getAsString();
    }

    private String safeNestedString(JsonObject obj, String parent, String child) {
        JsonObject parentObj = obj.getAsJsonObject(parent);
        if (parentObj == null || parentObj.isJsonNull()) return "Unknown";
        JsonElement el = parentObj.get(child);
        return (el == null || el.isJsonNull()) ? "Unknown" : el.getAsString();
    }

    private String safeArrayFirst(JsonObject obj, String field) {
        JsonArray arr = obj.getAsJsonArray(field);
        if (arr != null && arr.size() > 0) return arr.get(0).getAsString();
        return "Unknown";
    }

    public Nation getRandomNation() { return nations.get((int) (Math.random() * nations.size())); }
}
