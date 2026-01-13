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
            String url = "https://restcountries.com/v3.1/all?fields=name,capital,region,tld,currencies,languages,flags";

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
            String capital = safeArrayString(obj, "capital", 0);
            String region = safeString(obj, "region");
            List<String> tlds = safeStringList(obj, "tld");
            Map<String, String> currencies = safeMap(obj, "currencies");
            Map<String, String> languages = safeMap(obj, "languages");

            String flagPng = "Unknown";
            if (obj.has("flags") && obj.get("flags").isJsonObject()) {
                flagPng = safeString(obj.getAsJsonObject("flags"), "png");
            }

            nations.add(new Nation(name, capital, region, tlds, currencies, languages, flagPng));
        }
    }

    // --- Helpers per parsing JSON ---
    private String safeString(JsonObject obj, String key) {
        JsonElement el = obj.get(key);
        return (el == null || el.isJsonNull()) ? "Unknown" : el.getAsString();
    }

    private String safeNestedString(JsonObject obj, String parent, String child) {
        if (!obj.has(parent) || obj.get(parent).isJsonNull()) return "Unknown";
        JsonObject o = obj.getAsJsonObject(parent);
        return o.has(child) && !o.get(child).isJsonNull() ? o.get(child).getAsString() : "Unknown";
    }

    private String safeArrayString(JsonObject obj, String key, int index) {
        if (!obj.has(key) || obj.get(key).isJsonNull()) return "Unknown";
        JsonArray array = obj.getAsJsonArray(key);
        if (array.size() > index) return array.get(index).getAsString();
        return "Unknown";
    }

    private List<String> safeStringList(JsonObject obj, String key) {
        List<String> list = new ArrayList<>();
        if (obj.has(key) && !obj.get(key).isJsonNull()) {
            JsonArray arr = obj.getAsJsonArray(key);
            for (JsonElement e : arr) list.add(e.getAsString());
        }
        return list;
    }

    private Map<String, String> safeMap(JsonObject obj, String key) {
        Map<String, String> map = new HashMap<>();
        if (obj.has(key) && obj.get(key).isJsonObject()) {
            JsonObject o = obj.getAsJsonObject(key);
            for (String k : o.keySet()) {
                JsonElement el = o.get(k);
                if (el.isJsonObject()) {
                    JsonObject inner = el.getAsJsonObject();
                    String name = inner.has("name") ? inner.get("name").getAsString() : "Unknown";
                    map.put(k, name);
                } else if (el.isJsonPrimitive()) {
                    map.put(k, el.getAsString());
                } else {
                    map.put(k, "Unknown");
                }
            }
        }
        return map;
    }


    public Nation getRandomNation() { return nations.get((int)(Math.random() * nations.size())); }
}
