package me.shib.java.app.telegram.bot.dictionbot;

import me.shib.java.lib.dictionary.service.DictionService;
import me.shib.java.lib.dictionary.service.DictionWord;
import me.shib.java.lib.telegram.bot.easybot.BotConfig;
import me.shib.java.lib.telegram.bot.easybot.BotModel;
import me.shib.java.lib.telegram.bot.service.TelegramBot;
import me.shib.java.lib.telegram.bot.service.TelegramBot.ChatAction;
import me.shib.java.lib.telegram.bot.types.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class DictionBotModel extends BotModel {

    private static final String[] noResult = {"Sorry xxxxxxxxxx, looks like I have a lot to learn.",
            "Please accept my apology, xxxxxxxxxx. I don't know what that means.",
            "I couldn't find the synonym for that word in my book.",
            "I am still updating my knowledge xxxxxxxxxx. Will learn soon. But I'm very sorry for now.",
            "Sorry xxxxxxxxxx, I couldn't figure it out.",
            "If that's really an english word I should have found it by now. I guess I'm not that good enough."};

    private DictionService dictionService;
    private TelegramBot bot;

    public DictionBotModel(BotConfig config) {
        super(config);
        bot = getBot();
        dictionService = new DictionService();
    }

    private boolean isValidText(String text) {
        return text != null && text.matches("^[A-Za-z0-9]+$");
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

    public Message onCommand(Message msg) {
        String text = msg.getText();
        if (text != null) {
            if (text.equalsIgnoreCase("/start") || text.equalsIgnoreCase("/help")) {
                try {
                    bot.sendChatAction(new ChatId(msg.getChat().getId()), ChatAction.typing);
                    return bot.sendMessage(new ChatId(msg.getChat().getId()), "Hi *" + getProperName(msg.getFrom()) + "*. My name is *Diction Bot* (DictionBot)."
                            + " Just type in any *English word* and I'll try to give you the best possible definition/description.\n"
                            + "Please give me the best possible rating here - [Click here to rate & review DictionBot](https://telegram.me/storebot?start=dictionbot)", ParseMode.Markdown);
                } catch (IOException e) {
                    return null;
                }
            }
        }
        return null;
    }

    public Message onMessageFromAdmin(Message msg) {
        return null;
    }

    private String getNoResultMessage(String name) {
        Random rand = new Random();
        return noResult[rand.nextInt(noResult.length)].replace("xxxxxxxxxx", "*" + name + "*");
    }

    public Message onReceivingMessage(Message msg) {
        try {
            String text = msg.getText();
            long sender = msg.getChat().getId();
            if ((text == null) || (text.split("\\s+").length > 1) || (!isValidText(text))) {
                return bot.sendMessage(new ChatId(sender), "Hello *" + getProperName(msg.getFrom()) + "*, please send a single english word that doesn't have any special characters.", ParseMode.Markdown, false, msg.getMessage_id());
            } else {
                DictionWord wordMatch = dictionService.getDictionWord(text);
                if (wordMatch != null) {
                    return bot.sendMessage(new ChatId(sender), wordMatch.toString(), null, false, msg.getMessage_id());
                } else {
                    return bot.sendMessage(new ChatId(sender), getNoResultMessage(getProperName(msg.getFrom())), ParseMode.Markdown, false, msg.getMessage_id());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean onInlineQuery(InlineQuery query) {
        String wordToFind = query.getQuery();
        if ((wordToFind != null) && (wordToFind.split("\\s+").length == 1) && (isValidText(wordToFind))) {
            DictionWord wordMatch = dictionService.getDictionWord(wordToFind);
            if (wordMatch != null) {
                ArrayList<DictionWord.DictionDesc> descriptions = wordMatch.getDescriptions();
                InlineQueryResult[] results = new InlineQueryResult[descriptions.size()];
                for (int i = 0; i < descriptions.size(); i++) {
                    String id = "desc-" + i;
                    String title = descriptions.get(i).getWordType() + " - " + descriptions.get(i).getDescription();
                    String text = "*" + wordToFind + "* _(" + descriptions.get(i).getWordType() + ")_ - " + descriptions.get(i).getDescription();
                    InlineQueryResultArticle article = new InlineQueryResultArticle(id, title, text);
                    article.setParse_mode(ParseMode.Markdown);
                    results[i] = article;
                }
                try {
                    return bot.answerInlineQuery(query.getId(), results);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    @Override
    public boolean onChosenInlineResult(ChosenInlineResult chosenInlineResult) {
        return false;
    }

    public Message sendStatusMessage(long chatId) {
        return null;
    }

}
