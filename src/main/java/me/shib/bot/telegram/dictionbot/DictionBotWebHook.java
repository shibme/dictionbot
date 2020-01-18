package me.shib.bot.telegram.dictionbot;

import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class DictionBotWebHook extends TelegramWebhookBot {

    private transient String telegramBotToken;
    private transient String botUsername;
    private transient DictionBot dictionBot;

    public DictionBotWebHook() throws TelegramApiException {
        this.telegramBotToken = DictionBotConfig.getConfig().getBotToken();
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
        return getBotUsername();
    }
}
