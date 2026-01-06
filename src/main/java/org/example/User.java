package org.example;

import java.util.HashSet;
import java.util.Set;

public class User {
    private long chatId;
    private String name;
    private int totalPoints;
    private Set<String> nationDex;

    public User(long chatId, String name) {
        this.chatId = chatId;
        this.name = name;
        this.totalPoints = 0;
        this.nationDex = new HashSet<>();
    }

    public long getChatId() {
        return chatId;
    }

    public String getName() {
        return name;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void addPoints(int points) {
        this.totalPoints += points;
    }

    public Set<String> getNationDex() {
        return nationDex;
    }

    public void addNation(String nation) {
        nationDex.add(nation);
    }
}
