package me.shib.bot.telegram.dictionbot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public final class DictionBotLongPoll extends TelegramLongPollingBot {

    private final transient String telegramBotToken;
    private final transient String botUsername;
    private final transient DictionBot dictionBot;

    DictionBotLongPoll() throws TelegramApiException {
        this.telegramBotToken = DictionBotConfig.getConfig().getBotToken();
        this.botUsername = getMe().getUserName();
        this.dictionBot = new DictionBot(this);
    }

    @Override
    public void onUpdateReceived(Update update) {
        BotApiMethod botApiMethod = dictionBot.onUpdate(update);
        if (botApiMethod != null) {
            try {
                execute(botApiMethod);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return this.botUsername;
    }

    @Override
    public String getBotToken() {
        return this.telegramBotToken;
    }

}
