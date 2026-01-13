package org.example;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class MyTelegramBot implements LongPollingSingleThreadUpdateConsumer {

    private final TelegramClient telegramClient;
    private final CommandHandler commandHandler;
    private final QuizService quizService;

    public MyTelegramBot(String botToken, UserService userService, NationService nationService) {
        this.telegramClient = new OkHttpTelegramClient(botToken);
        this.quizService = new QuizService(nationService, userService,telegramClient);
        this.commandHandler = new CommandHandler(userService,quizService, telegramClient);

    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String text = update.getMessage().getText();
            String response;

            if (text.startsWith("/")) {
                if ("/quiz".equals(text)) {
                    response = quizService.startQuiz(chatId);
                } else {
                    commandHandler.handleCommand(chatId, text);
                    return;
                }
            } else {
                response = quizService.handleAnswer(chatId, text);
            }

            try {
                telegramClient.execute(SendMessage.builder()
                        .chatId(chatId)
                        .text(response)
                        .build());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
