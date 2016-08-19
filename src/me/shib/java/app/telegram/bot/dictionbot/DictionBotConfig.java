package me.shib.java.app.telegram.bot.dictionbot;

import me.shib.java.lib.jbots.JBot;
import me.shib.java.lib.jbots.JBotConfig;

public final class DictionBotConfig extends JBotConfig {

    private static final String botApiToken = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
    private static final Class<? extends JBot> botModelClass = DictionBot.class;
    private static final int threadCount = 4;
    private static final int reportInterval = 43200;
    private static final int minimumAllowedRating = 5;
    private static final boolean handleMissedChats = true;
    private static final boolean defaultWorker = true;
    private static final long[] admins = {00000000};

    @Override
    public int threadCount() {
        return threadCount;
    }

    @Override
    public int reportInterval() {
        return reportInterval;
    }

    @Override
    public int minimumAllowedRating() {
        return minimumAllowedRating;
    }

    @Override
    public boolean handleMissedChats() {
        return handleMissedChats;
    }

    @Override
    public boolean defaultWorker() {
        return defaultWorker;
    }

    @Override
    protected long[] admins() {
        return admins;
    }

    @Override
    public String botApiToken() {
        return botApiToken;
    }

    @Override
    public Class<? extends JBot> botModelClass() {
        return botModelClass;
    }
}
