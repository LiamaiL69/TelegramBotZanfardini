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

    // Avvia il quiz
    public String startQuiz(long chatId) {
        Nation nation = nationService.getRandomNation();
        List<String> hints = buildHints(nation);

        QuizSession session = new QuizSession(nation, hints);
        activeQuizzes.put(chatId, session);

        return "🧠 *Quiz iniziato!*\n" +
                "Hai massimo " + MAX_HINTS + " indizi.\n\n" +
                "📌 Primo indizio:\n" + "*" + session.nextHint() + "*";
    }

    // Gestione risposta
    public String handleAnswer(long chatId, String answer) {
        QuizSession session = activeQuizzes.get(chatId);
        if (session == null) return "❗ *Nessun quiz attivo.*\nUsa /quiz per iniziarne uno.";

        Nation nation = session.getNation();

        if (session.checkAnswer(answer)) {
            int hintsUsed = Math.min(session.getHintsUsed(), MAX_HINTS);
            int points = calculatePoints(hintsUsed);

            User user = userService.getOrCreateUser(chatId, "Player" + chatId);
            user.addPoints(points);
            userService.updatePoints(user);
            userService.addNationToDex(user, nation.getName());

            activeQuizzes.remove(chatId);

            return "✅ *Corretto!* 🎉\n\n" +
                    "📌 Era: *" + nation.getName() + "*\n" +
                    "⭐ Punti ottenuti: " + points + "\n" +
                    "🏅 Punti totali: " + user.getTotalPoints();
        } else {
            if (session.getHintsUsed() >= MAX_HINTS) {
                activeQuizzes.remove(chatId);
                return "❌ *Hai esaurito i 5 indizi!* 😢\n" +
                        "📌 La risposta era: *" + nation.getName() + "*";
            }

            String nextHint = session.nextHint();
            if (nextHint == null) {
                activeQuizzes.remove(chatId);
                return "❌ *Hai finito tutti gli indizi!* 😢\n" +
                        "📌 La risposta era: *" + nation.getName() + "*";
            }

            return "❌ *Sbagliato!* ⚠️\n\n" +
                    "📌 Nuovo indizio (" + session.getHintsUsed() + "/" + MAX_HINTS + "):\n" +
                    "*" + nextHint + "*";
        }
    }

    // Costruisce la lista di hints
    private List<String> buildHints(Nation nation) {
        List<String> hints = new ArrayList<>();

        // Hint principali
        hints.add("🌍 *Continente:* " + nation.getRegion());
        if (!nation.getCapital().equals("Unknown"))
            hints.add("🏙️ *Capitale:* " + nation.getCapital());
        if (!nation.getTopLevelDomain().isEmpty())
            hints.add("🌐 *TLD(TopLevelDomain):* " + nation.getTopLevelDomain().get(0));

        // Hint più facili
        hints.add("🔤 *Prima lettera del paese:* " + nation.getName().charAt(0));
        hints.add("📏 *Numero di lettere del nome della nazione:* " + nation.getName().length());

        Collections.shuffle(hints);
        return hints;
    }

    // Calcolo punti in base agli hints utilizzati
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
}
