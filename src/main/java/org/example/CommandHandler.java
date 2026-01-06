package org.example;

import org.example.User;
import org.example.UserService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

public class CommandHandler {
    private final UserService userService;
    private final TelegramClient telegramClient;

    public CommandHandler(UserService userService, TelegramClient telegramClient) {
        this.userService = userService;
        this.telegramClient = telegramClient;
    }

    public void handleCommand(long chatId, String command) {
        User user = userService.getOrCreateUser(chatId, "Player" + chatId);
        String commandBase = command.split(" ")[0];
        String text;

        switch (commandBase) {
            case "/start":
                text = "🤖 *Benvenuto in NationQuiz!*\n\n" +
                        "Scrivi /quiz per iniziare a giocare!\n\n" +
                        "⚠️ _DISCLAIMER_: I NOMI DELLE NAZIONI SONO IN INGLESE";
                break;

            case "/help":
                text = "📖 *Comandi disponibili:*\n\n" +
                        "🤖 /start - Messaggio di benvenuto\n" +
                        "🎲 /quiz - Inizia un nuovo quiz\n" +
                        "❓ /help - Mostra questo messaggio\n" +
                        "🏆 /leaderboard - Mostra la classifica punti\n" +
                        "📘 /NationDex - Le nazioni che hai scoperto\n" +
                        "✏️ /setname <nome> - Cambia il tuo username";
                break;

            case "/setname":
                String[] parts = command.split(" ", 2);
                if (parts.length < 2 || parts[1].isBlank()) {
                    text = "⚠️ Uso corretto: /setname <nuovo_nome>";
                } else {
                    String newName = parts[1].trim();
                    userService.updateUsername(user, newName);
                    text = "✅ *Nome aggiornato!*\nOra sei: *" + newName + "*";
                }
                break;

            case "/leaderboard":
                text = "🏆 *Leaderboard*\n\n" + userService.getLeaderboardString();
                break;

            case "/NationDex":
                text = "📘 *NationDex – Nazioni scoperte:*\n\n";
                if (user.getNationDex().isEmpty()) {
                    text += "_Nessuna nazione ancora scoperta!_";
                } else {
                    int i = 1;
                    for (String nation : user.getNationDex()) {
                        text += i + ". " + nation + "\n";
                        i++;
                    }
                }
                break;

            default:
                text = "⚠️ Comando sconosciuto!\nUsa /help per la lista dei comandi.";
        }

        // Invio messaggio con Markdown
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
