package me.shib.bot.telegram.dictionbot;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public final class Launcher {

    public static void main(String[] args) throws TelegramApiException {
        ApiContextInitializer.init();
        if (DictionBotConfig.getConfig().getWebHookUrl() != null &&
                !DictionBotConfig.getConfig().getWebHookUrl().isEmpty()) {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DictionBotConfig.getConfig().getWebHookUrl(),
                    DictionBotConfig.getConfig().getInternalWebHookUrl());
            telegramBotsApi.registerBot(new DictionBotWebHook());
        } else {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
            telegramBotsApi.registerBot(new DictionBotLongPoll());
        }
    }

}
