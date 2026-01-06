package org.example;

import java.util.List;

public class Nation {
    private String name;
    private String capital;
    private String region;
    private List<String> topLevelDomain;

    public Nation(String name, String capital, String region, List<String> topLevelDomain) {
        this.name = name;
        this.capital = capital;
        this.region = region;
        this.topLevelDomain = topLevelDomain;
    }

    public String getName() { return name; }
    public String getCapital() { return capital; }
    public String getRegion() { return region; }
    public List<String> getTopLevelDomain() { return topLevelDomain; }
}
