package me.shib.bot.telegram.dictionbot;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public final class Launcher {

    private static final String telegramBotApiTokenEnv = "TELEGRAM_BOT_TOKEN";

    private static synchronized String getTelegramBotToken() {
        String botToken = System.getenv(telegramBotApiTokenEnv);
        if (null == botToken || botToken.isEmpty()) {
            System.out.println("Please provide a valid Telegram Bot Token through "
                    + telegramBotApiTokenEnv + " environment variable.");
            System.exit(1);
        }
        return botToken;
    }

    public static void main(String[] args) throws TelegramApiException {
        String telegramBotToken = getTelegramBotToken();
        ApiContextInitializer.init();
        DictionBot bot = new DictionBot(telegramBotToken);
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        telegramBotsApi.registerBot(bot);
    }

}
