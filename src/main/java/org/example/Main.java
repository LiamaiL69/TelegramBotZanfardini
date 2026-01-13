package org.example;

import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

public class Main {
    static void main(String[] args) {
        // Carica token da config.properties
        String botToken = ConfigurationSingleton.getInstance().getProperty("BOT_TOKEN");

        // Crea servizi
        UserService userService = new UserService();
        NationService nationService = new NationService();

        try (TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication()) {
            botsApplication.registerBot(botToken, new MyTelegramBot(botToken, userService, nationService));
            System.out.println("✅ NationQuiz Bot avviato!");
            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
