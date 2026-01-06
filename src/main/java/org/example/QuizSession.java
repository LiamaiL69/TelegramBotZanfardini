package org.example;

import java.util.List;

public class QuizSession {

    private final Nation nation;
    private final List<String> hints;
    private int currentHintIndex = 0;

    public QuizSession(Nation nation, List<String> hints) {
        this.nation = nation;
        this.hints = hints;
    }

    public String nextHint() {
        if (currentHintIndex < hints.size()) {
            return hints.get(currentHintIndex++);
        }
        return null;
    }

    public boolean checkAnswer(String answer) {
        return nation.getName().equalsIgnoreCase(answer.trim());
    }

    public int getHintsUsed() {
        return currentHintIndex;
    }

    public Nation getNation() {
        return nation;
    }
}
