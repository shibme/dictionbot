package me.shib.bot.telegram.dictionbot;

final class DictionBotConfig {

    private static final String telegramBotTokenEnv = "TELEGRAM_BOT_TOKEN";
    private static final String telegramBotWebHookUrlEnv = "TELEGRAM_BOT_WEBHOOK_URL";
    private static final int port = 3428;
    private static final String localUrl = "http://0.0.0.0:" + port;

    private static DictionBotConfig dictionBotConfig;

    private String botToken;
    private String webHookUrl;

    private DictionBotConfig() {
        this.webHookUrl = System.getenv(telegramBotWebHookUrlEnv);
        this.botToken = System.getenv(telegramBotTokenEnv);
        if (null == botToken || botToken.isEmpty()) {
            System.out.println("Please provide a valid Telegram Bot Token through "
                    + telegramBotTokenEnv + " environment variable.");
            System.exit(1);
        }
    }

    static synchronized DictionBotConfig getConfig() {
        if (dictionBotConfig == null) {
            dictionBotConfig = new DictionBotConfig();
        }
        return dictionBotConfig;
    }

    String getBotToken() {
        return botToken;
    }

    String getWebHookUrl() {
        return webHookUrl;
    }

    String getInternalWebHookUrl() {
        return localUrl;
    }
}
