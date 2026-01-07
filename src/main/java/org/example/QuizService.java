package org.example;

import java.util.*;

public class QuizService {

    private final NationService nationService;
    private final UserService userService;
    private final Map<Long, QuizSession> activeQuizzes = new HashMap<>();
    private static final int MAX_HINTS = 5;

    public QuizService(NationService nationService, UserService userService) {
        this.nationService = nationService;
        this.userService = userService;
    }

    public String startQuiz(long chatId) {
        User user = userService.getOrCreateUser(chatId, "Player" + chatId);

        user.incrementQuizPlayed();           // aumenta quiz giocati
        userService.updateStats(user);        // salva statistiche nel DB

        Nation nation = nationService.getRandomNation();
        List<String> hints = buildHints(nation);

        QuizSession session = new QuizSession(nation, hints);
        activeQuizzes.put(chatId, session);

        return "🧠 *Quiz iniziato!* Hai massimo " + MAX_HINTS + " indizi.\n\n" +
                session.nextHint();
    }

    public String handleAnswer(long chatId, String answer) {
        QuizSession session = activeQuizzes.get(chatId);
        if (session == null) return "❗ Nessun quiz attivo. Usa /quiz per iniziarne uno.";

        User user = userService.getOrCreateUser(chatId, "Player" + chatId);

        if (session.checkAnswer(answer)) {
            int hintsUsed = Math.min(session.getHintsUsed(), MAX_HINTS);
            int points = calculatePoints(hintsUsed);

            user.addPoints(points);
            user.incrementQuizWon();             // incrementa vittorie
            userService.updateStats(user);       // salva tutto sul DB
            userService.addNationToDex(user, session.getNation().getName());

            activeQuizzes.remove(chatId);

            return "✅ *Corretto!* Era: " + session.getNation().getName() +
                    "\n⭐ Punti ottenuti: " + points +
                    "\n🏅 Punti totali: " + user.getTotalPoints() +
                    "\n🎯 Quiz vinti: " + user.getQuizWon() +
                    "\n🎲 Quiz giocati: " + user.getQuizPlayed();
        } else {
            if (session.getHintsUsed() >= MAX_HINTS) {
                activeQuizzes.remove(chatId);
                return "❌ Hai esaurito i 5 indizi!\nLa risposta era: " +
                        session.getNation().getName();
            }

            String nextHint = session.nextHint();
            if (nextHint == null) {
                activeQuizzes.remove(chatId);
                return "❌ Hai finito tutti gli indizi!\nLa risposta era: " +
                        session.getNation().getName();
            }

            return "❌ Sbagliato!\n\nNuovo indizio (" + session.getHintsUsed() + "/" + MAX_HINTS + "):\n" +
                    nextHint;
        }
    }

    private List<String> buildHints(Nation nation) {
        List<String> hints = new ArrayList<>();

        hints.add("🌍 Continente: " + nation.getRegion());
        if (!nation.getCapital().equals("Unknown"))
            hints.add("🏙️ Capitale: " + nation.getCapital());
        if (!nation.getTopLevelDomain().isEmpty())
            hints.add("🌐 Top Level Domain: " + nation.getTopLevelDomain().get(0));

        // Hint facili
        hints.add("🔤 Prima lettera del paese: " + nation.getName().charAt(0));
        hints.add("📏 Numero lettere del nome: " + nation.getName().length());
        hints.add("🔀 Nome mescolato: " + shuffleString(nation.getName()));

        int wordCount = nation.getName().trim().split("\\s+").length;
        hints.add("📝 Numero di parole nel nome: " + wordCount);

        Collections.shuffle(hints);
        return hints;
    }

    private int calculatePoints(int hintsUsed) {
        switch (hintsUsed) {
            case 1: return 15;
            case 2: return 12;
            case 3: return 9;
            case 4: return 6;
            case 5: return 3;
            default: return 1;
        }
    }

    private String shuffleString(String input) {
        List<Character> characters = new ArrayList<>();
        for (char c : input.toCharArray()) {
            characters.add(c);
        }
        Collections.shuffle(characters);
        StringBuilder sb = new StringBuilder();
        for (char c : characters) {
            sb.append(c);
        }
        return sb.toString();
    }
}
