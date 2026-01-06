package org.example;

import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

public class Main {
    public static void main(String[] args) {
        // Carica token e API key da config.properties
        String botToken = ConfigurationSingleton.getInstance().getProperty("BOT_TOKEN");
        String apiKey = ConfigurationSingleton.getInstance().getProperty("COUNTRYLAYER_API_KEY");

        // Crea servizi
        UserService userService = new UserService();
        NationService nationService = new NationService(apiKey);

        try (TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication()) {
            // Registra il bot passando i servizi necessari
            botsApplication.registerBot(botToken, new MyTelegramBot(botToken, userService, nationService));
            System.out.println("✅ NationQuiz Bot avviato!");
            Thread.currentThread().join(); // mantieni il bot in esecuzione
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
