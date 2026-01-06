package org.example;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class CommandHandler {

    private final UserService userService;
    private final QuizService quizService;
    private final TelegramClient telegramClient;

    public CommandHandler(UserService userService, QuizService quizService, TelegramClient telegramClient) {
        this.userService = userService;
        this.quizService = quizService;
        this.telegramClient = telegramClient;
    }

    public void handleCommand(long chatId, String command) {
        User user = userService.getOrCreateUser(chatId, "Player" + chatId);

        String commandBase = command.split(" ")[0];
        String text;

        switch (commandBase) {

            case "/start":
                text = "🤖 *Benvenuto in NationQuiz!* \n\n" +
                        "Scrivi /quiz per iniziare a giocare!\n\n" +
                        "_DISCLAIMER: I nomi delle nazioni sono in inglese_";
                break;

            case "/help":
                text = "📖 *Comandi disponibili:*\n\n" +
                        "🤖 /start - Messaggio di benvenuto\n" +
                        "🎲 /quiz - Inizia un nuovo quiz\n" +
                        "❓ /help - Mostra questo messaggio\n" +
                        "🏆 /leaderboard - Classifica punti\n" +
                        "📘 /NationDex - Nazioni scoperte\n" +
                        "✏️ /setname <nome> - Cambia il tuo username\n" +
                        "📊 /quizstats - Visualizza le tue statistiche quiz\n" +
                        "🎖️ /myrank - La tua posizione nella classifica\n" +
                        "♻️ /resetdex - Resetta il tuo NationDex";
                break;

            case "/setname":
                String[] parts = command.split(" ", 2);
                if (parts.length < 2 || parts[1].isBlank()) {
                    text = "⚠️ Uso corretto: /setname <nuovo_nome>";
                } else {
                    String newName = parts[1].trim();
                    userService.updateUsername(user, newName);
                    text = "✅ Il tuo nome è stato aggiornato a: *" + newName + "*";
                }
                break;

            case "/leaderboard":
                text = userService.getLeaderboardString();
                break;

            case "/NationDex":
                text = "📘 *NationDex – Nazioni scoperte:*\n\n";
                if (user.getNationDex().isEmpty()) {
                    text += "_Nessuna nazione ancora scoperta!_";
                } else {
                    for (String nation : user.getNationDex()) {
                        text += "• " + nation + "\n";
                    }
                }
                break;

            case "/quizstats":
                text = "📊 *Le tue statistiche:*\n\n" +
                        "⭐ Punti totali: " + user.getTotalPoints() + "\n" +
                        "🎲 Quiz giocati: " + user.getQuizPlayed() + "\n" +
                        "🎯 Quiz vinti: " + user.getQuizWon() + "\n" +
                        "📘 Nazioni scoperte: " + user.getNationDex().size();
                break;

            case "/myrank":
                int rank = userService.getUserRank(user);
                if (rank == -1) {
                    text = "⚠️ Non sei presente nella classifica!";
                } else {
                    text = "🎖️ *La tua posizione nella leaderboard:* \n\n" +
                            "Rank: " + rank + "\n" +
                            "Punti: " + user.getTotalPoints();
                }
                break;

            case "/resetdex":
                userService.resetNationDex(user);
                text = "♻️ *NationDex resettato!* Ora puoi ricominciare a scoprirle tutte!";
                break;

            default:
                text = "⚠️ Comando sconosciuto! Usa /help per la lista dei comandi.";
        }

        // invio messaggio formattato in Markdown
        try {
            telegramClient.execute(SendMessage.builder()
                    .chatId(chatId)
                    .text(text)
                    .parseMode("Markdown")
                    .build());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
