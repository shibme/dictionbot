package me.shib.java.app.telegram.bot.dictionbot;

import me.shib.java.lib.diction.DictionService;
import me.shib.java.lib.diction.DictionWord;
import me.shib.java.lib.jbots.JBot;
import me.shib.java.lib.jbots.JBotConfig;
import me.shib.java.lib.jbots.MessageHandler;
import me.shib.java.lib.jtelebot.types.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class DictionBot extends JBot {

    private static final String[] noResult = {"Sorry xxxxxxxxxx, looks like I have a lot to learn.",
            "Please accept my apology, xxxxxxxxxx. I don't know what that means.",
            "I couldn't find the synonym for that word in my book.",
            "I am still updating my knowledge xxxxxxxxxx. Will learn soon. But I'm very sorry for now.",
            "Sorry xxxxxxxxxx, I couldn't figure it out.",
            "If that's really an english word I should have found it by now. I guess I'm not that good enough."};
    private static final String helpUsHTML = "Please <b>help us with a good</b> /rating <b>or</b> /review for our work.";

    private static Logger logger = Logger.getLogger(DictionBot.class.getName());

    private DictionService dictionService;

    public DictionBot(JBotConfig config) {
        super(config);
        dictionService = new DictionService();
    }

    private String toHTMLFormatting(DictionWord word) {
        StringBuilder dictionBuilder = new StringBuilder();
        List<DictionWord.DictionDesc> descriptions = word.getDescriptions();
        if (descriptions.size() > 0) {
            dictionBuilder.append("<b>").append(word.getWord()).append(":\n\nDescription:\n</b>");
            for (DictionWord.DictionDesc description : descriptions) {
                dictionBuilder.append("<i>").append(description.getWordType()).append("</i>").append(" - ").append(description.getDescription()).append("\n");
            }
        }
        if (dictionBuilder.toString().isEmpty()) {
            return null;
        }
        return dictionBuilder.toString();
    }

    @Override
    public MessageHandler onMessage(Message message) {
        return new MessageHandler(message) {
            @Override
            public boolean onCommandFromAdmin(String command) {
                return onCommandFromUser(command);
            }

            @Override
            public boolean onCommandFromUser(String command) {
                if (message.getText().equalsIgnoreCase("/start") || message.getText().equalsIgnoreCase("/help")) {
                    try {
                        bot.sendChatAction(new ChatId(message.getChat().getId()), ChatAction.typing);
                        bot.sendMessage(new ChatId(message.getChat().getId()), "Hi <b>" + getProperName(message.getFrom()) + "</b>. My name is <b>"
                                + getProperName(bot.getIdentity()) + "</b> (@" + bot.getIdentity().getUsername() + ")."
                                + " Just type in any <b>English word</b> and I'll try to give you the best possible definition/description.\n"
                                + helpUsHTML, false, ParseMode.HTML);
                        return true;
                    } catch (IOException e) {
                        logger.throwing(this.getClass().getName(), "onCommand", e);
                    }
                }
                return false;
            }

            @Override
            public boolean onMessageFromAdmin() {
                return onMessageFromUser();
            }

            @Override
            public boolean onMessageFromUser() {
                try {
                    String text = message.getText();
                    long sender = message.getChat().getId();
                    if ((text == null) || (text.split("\\s+").length > 1) || (!isValidText(text))) {
                        bot.sendMessage(new ChatId(sender), "Hello *" + getProperName(message.getFrom()) + "*, please send a single english word that doesn't have any special characters.", false, ParseMode.Markdown, false, message.getMessage_id());
                    } else {
                        DictionWord wordMatch = dictionService.getDictionWord(text);
                        if (wordMatch != null) {
                            bot.sendMessage(new ChatId(sender), toHTMLFormatting(wordMatch) + "\n" + helpUsHTML, false, ParseMode.HTML, true, message.getMessage_id());
                        } else {
                            bot.sendMessage(new ChatId(sender), getNoResultMessage(getProperName(message.getFrom())), false, ParseMode.Markdown, false, message.getMessage_id());
                        }
                    }
                    return true;
                } catch (Exception e) {
                    logger.throwing(this.getClass().getName(), "onReceivingMessage", e);
                }
                return false;
            }
        };
    }

    private String getNoResultMessage(String name) {
        Random rand = new Random();
        return noResult[rand.nextInt(noResult.length)].replace("xxxxxxxxxx", "*" + name + "*");
    }

    private boolean isValidText(String text) {
        return text != null && text.matches("^[A-Za-z0-9]+$");
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
                    String text = "<b>" + wordToFind + "</b> <i>(" + descriptions.get(i).getWordType() + ")</i> - " + descriptions.get(i).getDescription()
                            + "\n\n" + helpUsHTML;
                    InlineQueryResultArticle article = new InlineQueryResultArticle(id, title, text);
                    article.setParse_mode(ParseMode.HTML);
                    article.disableWebPagePreview(true);
                    results[i] = article;
                }
                try {
                    return bot.answerInlineQuery(query.getId(), results);
                } catch (IOException e) {
                    logger.throwing(this.getClass().getName(), "onInlineQuery", e);
                }
            }
        }
        return false;
    }

    @Override
    public boolean onChosenInlineResult(ChosenInlineResult chosenInlineResult) {
        return false;
    }

}
