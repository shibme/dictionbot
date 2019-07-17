package me.shib.bot.telegram.dictionbot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public final class DictionBot extends TelegramLongPollingBot {

    private transient String telegramBotToken;
    private transient String botUsername;
    private transient DictionBotEngine updateHandler;

    public DictionBot(String telegramBotToken) throws TelegramApiException {
        this.telegramBotToken = telegramBotToken;
        this.botUsername = getMe().getUserName();
        this.updateHandler = new DictionBotEngine(this);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.getMessage() != null) {
            try {
                updateHandler.onMessage(update.getMessage());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else if (update.getInlineQuery() != null) {
            updateHandler.onInlineQuery(update.getInlineQuery());
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
