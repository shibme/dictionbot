package me.shib.bot.telegram.dictionbot;

import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class DictionBotWebHook extends TelegramWebhookBot {

    private final transient String telegramBotToken;
    private final transient String telegramBotPath;
    private final transient String botUsername;
    private final transient DictionBot dictionBot;

    public DictionBotWebHook() throws TelegramApiException {
        this.telegramBotToken = DictionBotConfig.getConfig().getBotToken();
        this.telegramBotPath = DictionBotConfig.getConfig().getWebHookBotPath();
        this.botUsername = getMe().getUserName();
        this.dictionBot = new DictionBot(this);
    }

    @Override
    public BotApiMethod onWebhookUpdateReceived(Update update) {
        return dictionBot.onUpdate(update);
    }

    @Override
    public String getBotUsername() {
        return this.botUsername;
    }

    @Override
    public String getBotToken() {
        return this.telegramBotToken;
    }

    @Override
    public String getBotPath() {
        return this.telegramBotPath;
    }
}
