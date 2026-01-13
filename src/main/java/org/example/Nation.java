package org.example;

import java.util.List;
import java.util.Map;

public class Nation {
    private String name;
    private String capital;
    private String region;
    private List<String> topLevelDomain;
    private Map<String, String> currencies; // chiave: codice, valore: nome valuta
    private Map<String, String> languages;  // chiave: codice, valore: lingua
    private String flagPng; // URL PNG bandiera

    public Nation(String name, String capital, String region, List<String> topLevelDomain,
                  Map<String, String> currencies, Map<String, String> languages, String flagPng) {
        this.name = name;
        this.capital = capital;
        this.region = region;
        this.topLevelDomain = topLevelDomain;
        this.currencies = currencies;
        this.languages = languages;
        this.flagPng = flagPng;
    }

    // --- Getters ---
    public String getName() { return name; }
    public String getCapital() { return capital; }
    public String getRegion() { return region; }
    public List<String> getTopLevelDomain() { return topLevelDomain; }
    public Map<String, String> getCurrencies() { return currencies; }
    public Map<String, String> getLanguages() { return languages; }
    public String getFlagPng() { return flagPng; }
}
