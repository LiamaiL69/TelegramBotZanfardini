package org.example;

import java.util.List;
import java.util.Map;

public class Nation {

    private final String name;
    private final String capital;
    private final String region;
    private final List<String> topLevelDomain;
    private final String flagUrl;


    private final Map<String, String> currencies;
    private final Map<String, String> languages;

    public Nation(String name, String capital, String region,
                  List<String> topLevelDomain, String flagUrl,
                  Map<String, String> currencies, Map<String, String> languages) {
        this.name = name;
        this.capital = capital;
        this.region = region;
        this.topLevelDomain = topLevelDomain;
        this.flagUrl = flagUrl;
        this.currencies = currencies;
        this.languages = languages;
    }

    // --- Getters ---
    public String getName() { return name; }
    public String getCapital() { return capital; }
    public String getRegion() { return region; }
    public List<String> getTopLevelDomain() { return topLevelDomain; }
    public String getFlagUrl() { return flagUrl; }
    public Map<String, String> getCurrencies() { return currencies; }
    public Map<String, String> getLanguages() { return languages; }

}
