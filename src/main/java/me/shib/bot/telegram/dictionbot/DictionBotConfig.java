package me.shib.bot.telegram.dictionbot;

import java.util.Random;

final class DictionBotConfig {

    private static final String telegramBotTokenEnv = "TELEGRAM_BOT_TOKEN";
    private static final String telegramBotWebHookUrlEnv = "TELEGRAM_BOT_WEBHOOK_URL";
    private static final int port = 3428;
    private static final String localUrl = "http://0.0.0.0:" + port;

    private static DictionBotConfig dictionBotConfig;

    private String botToken;
    private String webHookUrl;
    private String webHookBotPath;

    private DictionBotConfig() {
        this.webHookUrl = System.getenv(telegramBotWebHookUrlEnv);
        this.botToken = System.getenv(telegramBotTokenEnv);
        if (null == botToken || botToken.isEmpty()) {
            System.out.println("Please provide a valid Telegram Bot Token through "
                    + telegramBotTokenEnv + " environment variable.");
            System.exit(1);
        }
        this.webHookBotPath = randomString();
    }

    static synchronized DictionBotConfig getConfig() {
        if (dictionBotConfig == null) {
            dictionBotConfig = new DictionBotConfig();
        }
        return dictionBotConfig;
    }

    private String randomString() {
        Random random = new Random();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        char[] text = new char[128];
        for (int i = 0; i < 128; i++) {
            text[i] = characters.charAt(random.nextInt(characters.length()));
        }
        return new String(text);
    }

    String getBotToken() {
        return botToken;
    }

    String getWebHookUrl() {
        return webHookUrl;
    }

    String getWebHookBotPath() {
        return webHookBotPath;
    }

    String getInternalWebHookUrl() {
        return localUrl;
    }
}
