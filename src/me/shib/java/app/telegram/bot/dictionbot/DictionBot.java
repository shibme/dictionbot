package me.shib.java.app.telegram.bot.dictionbot;

import me.shib.java.lib.diction.DictionService;
import me.shib.java.lib.diction.DictionWord;
import me.shib.java.lib.jbots.JBot;
import me.shib.java.lib.jbots.JBotConfig;
import me.shib.java.lib.jbots.MessageHandler;
import me.shib.java.lib.jtelebot.models.inline.InlineQueryResult;
import me.shib.java.lib.jtelebot.models.inline.InlineQueryResultArticle;
import me.shib.java.lib.jtelebot.models.inline.InputTextMessageContent;
import me.shib.java.lib.jtelebot.models.types.ChatAction;
import me.shib.java.lib.jtelebot.models.types.ChatId;
import me.shib.java.lib.jtelebot.models.types.ParseMode;
import me.shib.java.lib.jtelebot.models.updates.CallbackQuery;
import me.shib.java.lib.jtelebot.models.updates.ChosenInlineResult;
import me.shib.java.lib.jtelebot.models.updates.InlineQuery;
import me.shib.java.lib.jtelebot.models.updates.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public final class DictionBot extends JBot {

    private static final String[] noResult = {"Sorry xxxxxxxxxx, looks like I have a lot to learn.",
            "Please accept my apology, xxxxxxxxxx. I don't know what that means.",
            "I couldn't find the synonym for that word in my book.",
            "I am still updating my knowledge xxxxxxxxxx. Will learn soon. But I'm very sorry for now.",
            "Sorry xxxxxxxxxx, I couldn't figure it out.",
            "If that's really an english word I should have found it by now. I guess I'm not that good enough."};
    private static final String helpUsHTML = "Please <b>help us with a good</b> /rating <b>or</b> /review for our work.";

    private static Logger logger = Logger.getLogger(DictionBot.class.getName());

    private DictionService dictionService;
    private String helpUsHTMLWithLink;

    public DictionBot(JBotConfig config) {
        super(config);
        this.dictionService = new DictionService();
        this.helpUsHTMLWithLink = "Please <b>help us with a good</b> <a href=\"https://telegram.me/" + bot().getIdentity().getUsername() + "?start=review\">rating or review</a> for our work.";
    }

    private String toHTMLFormatting(DictionWord dictionWord) {
        StringBuilder dictionBuilder = new StringBuilder();
        List<DictionWord.DictionDesc> descriptions = dictionWord.getDescriptions();
        if (descriptions.size() > 0) {
            dictionBuilder.append("<b>").append(dictionWord.getWord().toLowerCase()).append(":\n</b>");
            for (DictionWord.DictionDesc description : descriptions) {
                dictionBuilder.append("<i>").append(description.getWordType()).append("</i>").append(" - ").append(description.getDescription()).append("\n");
            }
        }
        List<String> hyponyms = dictionWord.getHyponyms();
        if (hyponyms.size() > 0) {
            dictionBuilder.append("\n<b>Related words:").append("\n</b>");
            for (int i = 0; i < hyponyms.size(); i++) {
                dictionBuilder.append("<i>").append(hyponyms.get(i)).append("</i>");
                if (i < (hyponyms.size() - 1)) {
                    dictionBuilder.append(" - ");
                }
            }
            dictionBuilder.append("\n");
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
            public boolean onCommandFromAdmin(String command, String argument) {
                return onCommandFromUser(command, argument);
            }

            @Override
            public boolean onCommandFromUser(String command, String argument) {
                if (command.equalsIgnoreCase("/start") || command.equalsIgnoreCase("/help")) {
                    try {
                        bot().sendChatAction(new ChatId(message.getChat().getId()), ChatAction.typing);
                        bot().sendMessage(new ChatId(message.getChat().getId()), "Hi <b>" + getProperName(message.getFrom()) + "</b>. My name is <b>"
                                + getProperName(bot().getIdentity()) + "</b> (@" + bot().getIdentity().getUsername() + ")."
                                + " Just type in any <b>English word</b> and I'll try to give you the best possible definition/description.\n"
                                + helpUsHTML, ParseMode.HTML);
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
                        bot().sendMessage(new ChatId(sender), "Hello *" + getProperName(message.getFrom()) + "*, please send a single english word that doesn't have any special characters.", ParseMode.Markdown, false, message.getMessage_id());
                    } else {
                        DictionWord wordMatch = dictionService.getDictionWord(text);
                        if (wordMatch != null) {
                            bot().sendMessage(new ChatId(sender), toHTMLFormatting(wordMatch) + "\n" + helpUsHTML, ParseMode.HTML, true, message.getMessage_id());
                        } else {
                            bot().sendMessage(new ChatId(sender), getNoResultMessage(getProperName(message.getFrom())), ParseMode.Markdown, false, message.getMessage_id());
                        }
                    }
                    return true;
                } catch (Exception e) {
                    logger.throwing(this.getClass().getName(), "onReceivingMessage", e);
                    return false;
                }
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
    public void onInlineQuery(InlineQuery query) {
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
                            + "\n\n" + helpUsHTMLWithLink;
                    InputTextMessageContent inputTextMessageContent = new InputTextMessageContent(text);
                    inputTextMessageContent.setParse_mode(ParseMode.HTML);
                    inputTextMessageContent.disableWebPagePreview();
                    InlineQueryResultArticle article = new InlineQueryResultArticle(id, title, inputTextMessageContent);
                    results[i] = article;
                }
                try {
                    bot().answerInlineQuery(query.getId(), results);
                } catch (IOException e) {
                    logger.throwing(this.getClass().getName(), "onInlineQuery", e);
                }
            }
        }
    }

    @Override
    public void onChosenInlineResult(ChosenInlineResult chosenInlineResult) {
    }

    @Override
    public void onCallbackQuery(CallbackQuery callbackQuery) {
    }

}
