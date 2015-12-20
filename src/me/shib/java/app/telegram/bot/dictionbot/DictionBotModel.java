package me.shib.java.app.telegram.bot.dictionbot;

import me.shib.java.lib.dictionary.service.DictionService;
import me.shib.java.lib.dictionary.service.DictionWord;
import me.shib.java.lib.telegram.bot.easybot.TBotModel;
import me.shib.java.lib.telegram.bot.service.TelegramBot;
import me.shib.java.lib.telegram.bot.service.TelegramBot.ChatAction;
import me.shib.java.lib.telegram.bot.types.ChatId;
import me.shib.java.lib.telegram.bot.types.Message;
import me.shib.java.lib.telegram.bot.types.ParseMode;
import me.shib.java.lib.telegram.bot.types.User;

import java.io.IOException;
import java.util.Random;

public class DictionBotModel implements TBotModel {

    private static final String[] noResult = {"Sorry xxxxxxxxxx, looks like I have a lot to learn.",
            "Please accept my apology, xxxxxxxxxx. I don't know what that means.",
            "I couldn't find the synonym for that word in my book.",
            "I am still updating my knowledge xxxxxxxxxx. Will learn soon. But I'm very sorry for now.",
            "Sorry xxxxxxxxxx, I couldn't figure it out.",
            "If that's really an english word I should have found it by now. I guess I'm not that good enough."};

    private static final String[] developerName = {"Shibly", "Meeran"};

    private DictionService dictionService;

    public DictionBotModel() {
        dictionService = new DictionService();
    }

    private boolean isValidText(String text) {
        if (text != null) {
            return text.matches("^[A-Za-z0-9]+$");
        }
        return false;
    }

    private String getProperName(User user) {
        if (user != null) {
            if (isValidText(user.getFirst_name())) {
                return user.getFirst_name();
            }
            if (isValidText(user.getLast_name())) {
                return user.getLast_name();
            }
            if (isValidText(user.getUsername())) {
                return user.getUsername();
            }
        }
        return "";
    }

    public Message onCommand(TelegramBot tbs, Message msg) {
        String text = msg.getText();
        if (text != null) {
            if (text.equalsIgnoreCase("/start") || text.equalsIgnoreCase("/help")) {
                try {
                    tbs.sendChatAction(new ChatId(msg.getChat().getId()), ChatAction.typing);
                    return tbs.sendMessage(new ChatId(msg.getChat().getId()), "Hi *" + getProperName(msg.getFrom()) + "*. My name is *Diction Bot* (DictionBot)."
                            + " Just type in any *English word* and I'll try to give you the best possible definition/description.\n"
                            + "Please give me the best possible rating here - [Click here to rate & review DictionBot](https://telegram.me/storebot?start=dictionbot)", ParseMode.Markdown);
                } catch (IOException e) {
                    return null;
                }
            }
        }
        return null;
    }

    public Message onMessageFromAdmin(TelegramBot tbs, Message msg) {
        return null;
    }

    private String getNoResultMessage(String name) {
        Random rand = new Random();
        return noResult[rand.nextInt(noResult.length)].replace("xxxxxxxxxx", "*" + name + "*");
    }

    private String getKnownUserMessage(String firstName, String lastName, String text) {
        for (String devName : developerName) {
            if (text.equalsIgnoreCase(devName)) {
                return "That guy gave me life. He doesn't want me to say anything more.";
            }
        }
        if (text.equalsIgnoreCase(firstName) || text.equalsIgnoreCase(lastName)) {
            return "Don't you know who you are?";
        }
        return null;
    }

    public Message onReceivingMessage(TelegramBot tbs, Message msg) {
        try {
            String text = msg.getText();
            long sender = msg.getChat().getId();
            User sendingUser = msg.getFrom();
            if ((text == null) || (text.split("\\s+").length > 1) || (!isValidText(text))) {
                return tbs.sendMessage(new ChatId(sender), "Hello *" + getProperName(msg.getFrom()) + "*, please send only a single english word that doesn't have any special characters.", ParseMode.Markdown, false, msg.getMessage_id());
            } else {
                String knownUserMessage = getKnownUserMessage(sendingUser.getFirst_name(), sendingUser.getLast_name(), text);
                if (knownUserMessage != null) {
                    return tbs.sendMessage(new ChatId(sender), knownUserMessage, ParseMode.None, false, msg.getMessage_id());
                } else {
                    DictionWord descr = dictionService.getDictionWord(text);
                    if (descr != null) {
                        return tbs.sendMessage(new ChatId(sender), descr.toString(), ParseMode.None, false, msg.getMessage_id());
                    } else {
                        return tbs.sendMessage(new ChatId(sender), getNoResultMessage(getProperName(msg.getFrom())), ParseMode.Markdown, false, msg.getMessage_id());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Message sendStatusMessage(TelegramBot tBotService, long chatId) {
        // TODO Auto-generated method stub
        return null;
    }

}
