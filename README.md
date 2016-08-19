# DictionBot
[![Build Status](https://travis-ci.org/shibme/dictionbot.svg)](https://travis-ci.org/shibme/dictionbot)
[![Dependency Status](https://www.versioneye.com/user/projects/56adffd17e03c7003db6962a/badge.svg?style=flat)](https://www.versioneye.com/user/projects/56adffd17e03c7003db6962a)
[![Percentage of issues still open](http://isitmaintained.com/badge/open/shibme/dictionbot.svg)](http://isitmaintained.com/project/shibme/dictionbot "Percentage of issues still open")

Telegram's [@DictionBot](https://telegram.me/DictionBot) - A simple English dictionary that describes almost any word

### Configuration for Bot Owners
Edit `DictionBotConfig.java` and update your bot token or configure based on your requirements
```java
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
```