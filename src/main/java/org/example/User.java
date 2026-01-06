package org.example;

import java.util.HashSet;
import java.util.Set;

public class User {
    private long chatId;
    private String name;
    private int totalPoints;
    private Set<String> nationDex;
    private int quizPlayed;
    private int quizWon;

    public User(long chatId, String name) {
        this.chatId = chatId;
        this.name = name;
        this.totalPoints = 0;
        this.nationDex = new HashSet<>();
        this.quizPlayed = 0;
        this.quizWon = 0;
    }

    // --- Getters e setters ---
    public long getChatId() { return chatId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getTotalPoints() { return totalPoints; }
    public void addPoints(int points) { this.totalPoints += points; }
    public Set<String> getNationDex() { return nationDex; }
    public void addNation(String nation) { nationDex.add(nation); }
    public void resetNationDex() { nationDex.clear(); }

    // --- Statistiche quiz ---
    public int getQuizPlayed() { return quizPlayed; }
    public int getQuizWon() { return quizWon; }
    public void incrementQuizPlayed() { this.quizPlayed++; }
    public void incrementQuizWon() { this.quizWon++; }
}
