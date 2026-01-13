package org.example;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.*;

public class QuizService {

    private final NationService nationService;
    private final UserService userService;
    private final Map<Long, QuizSession> activeQuizzes = new HashMap<>();
    private static final int MAX_HINTS = 5;
    private final TelegramClient telegramClient;

    public QuizService(NationService nationService, UserService userService, TelegramClient telegramClient) {
        this.nationService = nationService;
        this.userService = userService;
        this.telegramClient = telegramClient;
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
            user.incrementQuizWon();
            userService.updateStats(user);
            userService.addNationToDex(user, session.getNation().getName());

            activeQuizzes.remove(chatId);

            return "✅ *Corretto!* Era: " + session.getNation().getName() +
                    "\n⭐ Punti ottenuti: " + points +
                    "\n🏅 Punti totali: " + user.getTotalPoints() +
                    "\n🎯 Quiz vinti: " + user.getQuizWon() +
                    "\n🎲 Quiz giocati: " + user.getQuizPlayed()+
                    "\n   Per ricominciare rapidamente: /quiz";
        } else {
            if (session.getHintsUsed() >= MAX_HINTS) {
                activeQuizzes.remove(chatId);
                return "❌ Hai esaurito i " + MAX_HINTS + " indizi!\nLa risposta era: " +
                        session.getNation().getName()+ "\n   Per ricominciare rapidamente: /quiz";
            }

            String nextHint = session.nextHint();


            if (nextHint.equals("FLAG")) {
                sendFlag(chatId, session.getNation());
                return "Sbagliato!\n\nNuovo indizio\n\n🚩 *Ecco la bandiera!*";
            }

            return "❌ Sbagliato!\n\nNuovo indizio (" + session.getHintsUsed() + "/" + MAX_HINTS + "):\n" +
                    nextHint;
        }
    }

    private List<String> buildHints(Nation nation) {
        List<String> hints = new ArrayList<>();

        // Hint principali
        hints.add("🌍 Continente: " + nation.getRegion());
        if (!nation.getCapital().equals("Unknown"))
            hints.add("🏙️ Capitale: " + nation.getCapital());
        if (!nation.getTopLevelDomain().isEmpty())
            hints.add("🌐 Top Level Domain: " + nation.getTopLevelDomain().get(0));

        // Currency come hint
        if (!nation.getCurrencies().isEmpty()) {
            String currencyHint = nation.getCurrencies().entrySet().iterator().next().getValue(); // nome valuta
            hints.add("💰 Valuta principale: " + currencyHint);
        }

        // Lingua come hint
        if (!nation.getLanguages().isEmpty()) {
            String languageHint = nation.getLanguages().entrySet().iterator().next().getValue();
            hints.add("🗣️ Lingua principale: " + languageHint);
        }

        // Hint facili
        hints.add("🔤 Prima lettera del paese: " + nation.getName().charAt(0));
        hints.add("📏 Numero lettere del nome: " + nation.getName().length());
        hints.add("🔀 Nome mescolato: " + shuffleString(nation.getName()));
        hints.add("FLAG");
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
        for (char c : input.toCharArray()) characters.add(c);
        Collections.shuffle(characters);
        StringBuilder sb = new StringBuilder();
        for (char c : characters) sb.append(c);
        return sb.toString();
    }

    private void sendFlag(long chatId, Nation nation) {
        String flagUrl = nation.getFlagUrl();
        if (flagUrl.equals("Unknown")) return;

        SendPhoto photo = SendPhoto.builder()
                .chatId(chatId)
                .photo(new InputFile(flagUrl))
                .build();

        try {
            telegramClient.execute(photo);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
