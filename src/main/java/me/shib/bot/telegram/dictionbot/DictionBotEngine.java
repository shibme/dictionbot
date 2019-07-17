package me.shib.bot.telegram.dictionbot;

import me.shib.java.lib.diction.DictionService;
import me.shib.java.lib.diction.DictionWord;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Random;

public final class DictionBotEngine {

    private static final String invalidMessageResponse = "Please send a valid word to search the dictionary";
    private static final String[] noResult = {"Sorry xxxxxxxxxx, looks like I have a lot to learn.",
            "Please accept my apology, xxxxxxxxxx. I don't know what that means.",
            "I couldn't find the synonym for that word in my book.",
            "I am still updating my knowledge xxxxxxxxxx. Will learn soon. But I'm very sorry for now.",
            "Sorry xxxxxxxxxx, I couldn't figure it out.",
            "If that's really an english word I should have found it by now. I guess I'm not that good enough."};

    private transient DictionBot bot;
    private transient DictionService dictionService;

    public DictionBotEngine(DictionBot bot) {
        this.bot = bot;
        this.dictionService = new DictionService();
    }

    private static boolean isValidName(String text) {
        return text != null && !text.isEmpty();
    }

    private static String getProperName(String firstName, String lastName, String username) {
        StringBuilder nameBuilder = new StringBuilder();
        if (isValidName(firstName)) {
            nameBuilder.append(firstName);
        }
        if (isValidName(lastName)) {
            if (!nameBuilder.toString().isEmpty()) {
                nameBuilder.append(" ");
            }
            nameBuilder.append(lastName);
        }
        if (nameBuilder.toString().isEmpty() && isValidName(username)) {
            nameBuilder.append(username);
        }
        return nameBuilder.toString();
    }

    public static String getProperName(Chat chat) {
        if (chat != null) {
            return getProperName(chat.getFirstName(), chat.getLastName(), chat.getUserName());
        }
        return "";
    }

    public static String getProperName(User user) {
        if (user != null) {
            return getProperName(user.getFirstName(), user.getLastName(), user.getUserName());
        }
        return "";
    }

    private void sendTypingAction(Long chatId) throws TelegramApiException {
        SendChatAction sendChatAction = new SendChatAction().setAction(ActionType.TYPING).setChatId(chatId);
        bot.execute(sendChatAction);
    }

    private void sendMessage(Long chatId, String text, Integer replyToMessageId) throws TelegramApiException {
        sendTypingAction(chatId);
        SendMessage sendMessage = new SendMessage().setText(text)
                .setParseMode(ParseMode.HTML)
                .setChatId(chatId);
        if (replyToMessageId != null) {
            sendMessage.setReplyToMessageId(replyToMessageId);
        }
        bot.execute(sendMessage);
    }

    private boolean isValidText(String text) {
        return text != null && text.matches("^[A-Za-z0-9]+$");
    }

    private String getNoResultMessage(String name) {
        Random rand = new Random();
        return noResult[rand.nextInt(noResult.length)].replace("xxxxxxxxxx", "<b>" + name + "</b>");
    }

    public void onMessage(Message message) throws TelegramApiException {
        if (message.getText() != null) {
            User sender = message.getFrom();
            String text = message.getText();
            if (text.equalsIgnoreCase("/start") || text.equalsIgnoreCase("/help")) {
                sendMessage(message.getChatId(), "Hi <b>" + getProperName(sender) + "</b>. My name is <b>"
                        + getProperName(bot.getMe()) + "</b>. Just type in any <b>English word</b> and I'll " +
                        "try to give you the best possible definition/description.", message.getMessageId());
            } else {
                if ((text.split("\\s+").length > 1) || !isValidText(text)) {
                    sendMessage(message.getChatId(), "Hello <b>" + getProperName(message.getFrom()) +
                                    "</b>, please send a single english word that doesn't have any special characters.",
                            message.getMessageId());
                } else {
                    DictionWord wordMatch = dictionService.getDictionWord(text);
                    if (wordMatch != null) {
                        sendMessage(message.getChatId(), toHTMLFormatting(wordMatch), message.getMessageId());
                    } else {
                        sendMessage(message.getChatId(), getNoResultMessage(getProperName(message.getFrom())),
                                message.getMessageId());
                    }
                }
            }
        } else {
            SendMessage sendMessage = new SendMessage().setText(invalidMessageResponse).setChatId(message.getChatId());
            bot.execute(sendMessage);
        }
    }

    private String toHTMLFormatting(DictionWord dictionWord) {
        StringBuilder dictionBuilder = new StringBuilder();
        List<DictionWord.DictionDesc> descriptions = dictionWord.getDescriptions();
        if (descriptions.size() > 0) {
            dictionBuilder.append("<b>").append(dictionWord.getWord().toLowerCase()).append(":\n</b>");
            for (DictionWord.DictionDesc description : descriptions) {
                dictionBuilder.append("<i>").append(description.getWordType()).append("</i>")
                        .append(" - ").append(description.getDescription()).append("\n");
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

    public void onInlineQuery(InlineQuery query) {
        /*String wordToFind = query.getQuery();
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
        }*/
    }

}
