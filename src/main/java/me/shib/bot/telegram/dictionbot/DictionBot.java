package me.shib.bot.telegram.dictionbot;

import com.google.gson.Gson;
import me.shib.java.lib.diction.DictionService;
import me.shib.java.lib.diction.DictionWord;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

final class DictionBot {

    private static final int maxHypononyms = 20;
    private static final Gson gson = new Gson();
    private static final String invalidMessageResponse = "Please send a valid word to search the dictionary";
    private static final String[] noResult = {"Sorry xxxxxxxxxx, looks like I have a lot to learn.",
            "Please accept my apology, xxxxxxxxxx. I don't know what that means.",
            "I couldn't find the synonym for that word in my book.",
            "I am still updating my knowledge xxxxxxxxxx. Will learn soon. But I'm very sorry for now.",
            "Sorry xxxxxxxxxx, I couldn't figure it out.",
            "If that's really an english word I should have found it by now. I guess I'm not that good enough."};

    private transient DefaultAbsSender bot;
    private transient User botUser;
    private transient DictionService dictionService;

    DictionBot(DefaultAbsSender bot) throws TelegramApiException {
        this.bot = bot;
        this.botUser = this.bot.getMe();
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

    private static String getProperName(User user) {
        if (user != null) {
            return getProperName(user.getFirstName(), user.getLastName(), user.getUserName());
        }
        return "";
    }

    BotApiMethod onUpdate(Update update) {
        System.out.println("Update: " + gson.toJson(update));
        if (update.getMessage() != null) {
            try {
                return onMessage(update.getMessage());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else if (update.getInlineQuery() != null) {
            return onInlineQuery(update.getInlineQuery());
        }
        return null;
    }

    private void sendTypingAction(Long chatId) throws TelegramApiException {
        SendChatAction sendChatAction = new SendChatAction().setAction(ActionType.TYPING).setChatId(chatId);
        bot.execute(sendChatAction);
    }

    private BotApiMethod sendMessage(Long chatId, String text, Integer replyToMessageId) throws TelegramApiException {
        sendTypingAction(chatId);
        SendMessage sendMessage = new SendMessage().setText(text)
                .setParseMode(ParseMode.HTML)
                .setChatId(chatId);
        if (replyToMessageId != null) {
            sendMessage.setReplyToMessageId(replyToMessageId);
        }
        sendMessage.disableWebPagePreview();
        return sendMessage;
    }

    private boolean isValidWord(String word) {
        return word != null && word.matches("^[A-Za-z0-9]+$");
    }

    private String getNoResultMessage(String name) {
        Random rand = new Random();
        return noResult[rand.nextInt(noResult.length)].replace("xxxxxxxxxx", "<b>" + name + "</b>");
    }

    private BotApiMethod onMessage(Message message) throws TelegramApiException {
        if (message.getText() != null) {
            User sender = message.getFrom();
            String text = message.getText();
            if (text.equalsIgnoreCase("/start") || text.equalsIgnoreCase("/help")) {
                return sendMessage(message.getChatId(), "Hi <b>" + getProperName(sender) + "</b>. My name is <b>"
                        + getProperName(botUser) + "</b>. Just type in any <b>English word</b> and I'll " +
                        "try to give you the best possible definition/description.", message.getMessageId());
            } else {
                String word = null;
                if (text.toLowerCase().startsWith("/start") && text.split("\\s+").length == 2) {
                    word = text.split("\\s+")[1];
                } else if (text.split("\\s+").length == 1) {
                    word = text;
                }
                if (isValidWord(word)) {
                    DictionWord wordMatch = dictionService.getDictionWord(word);
                    if (wordMatch != null) {
                        return sendMessage(message.getChatId(), toHTMLFormatting(wordMatch),
                                message.getMessageId());
                    } else {
                        return sendMessage(message.getChatId(), getNoResultMessage(getProperName(message.getFrom())),
                                message.getMessageId());
                    }
                } else {
                    return sendMessage(message.getChatId(), "Hello <b>" + getProperName(message.getFrom()) +
                                    "</b>, please send a single english word that doesn't have any special characters.",
                            message.getMessageId());
                }
            }
        } else {
            return sendMessage(message.getChatId(), invalidMessageResponse, message.getMessageId());
        }
    }

    private String wordToRefUrl(String word, String linkText) {
        return "<a href=\"https://t.me/" + botUser.getUserName() + "?start=" + word + "\">" + linkText + "</a>";
    }

    private String wordToRefUrl(String word) {
        return wordToRefUrl(word, word);
    }

    private String getHypononymsFooter(DictionWord dictionWord) {
        StringBuilder hypononymsFooter = new StringBuilder();
        List<String> hyponyms = dictionWord.getHyponyms();
        if (hyponyms.size() > 0) {
            hypononymsFooter.append("\n<b>Related words:").append("\n</b>");
            int limit = hyponyms.size();
            if (limit > maxHypononyms) {
                limit = maxHypononyms;
            }
            for (int i = 0; i < limit; i++) {
                hypononymsFooter.append("<i>").append(wordToRefUrl(hyponyms.get(i))).append("</i>");
                if (i < (limit - 1)) {
                    hypononymsFooter.append(" - ");
                }
            }
        }
        return hypononymsFooter.toString();
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
        dictionBuilder.append(getHypononymsFooter(dictionWord));
        if (dictionBuilder.toString().isEmpty()) {
            return null;
        }
        return dictionBuilder.toString();
    }

    private BotApiMethod onInlineQuery(InlineQuery query) {
        String wordToFind = query.getQuery();
        if ((wordToFind != null) && (wordToFind.split("\\s+").length == 1) && (isValidWord(wordToFind))) {
            DictionWord wordMatch = dictionService.getDictionWord(wordToFind);
            List<InlineQueryResult> inlineQueryResults = new ArrayList<>();
            if (wordMatch != null) {
                List<DictionWord.DictionDesc> descriptions = wordMatch.getDescriptions();
                for (int i = 0; i < descriptions.size(); i++) {
                    String id = "desc-" + i;
                    String title = descriptions.get(i).getWordType() + " - " + descriptions.get(i).getDescription();
                    String text = "<b>" + wordToRefUrl(wordToFind) + "</b> <i>(" + descriptions.get(i).getWordType() +
                            ")</i> - " + descriptions.get(i).getDescription() + "\n\n<b>" +
                            wordToRefUrl(wordToFind, "Show More Definitions") + "</b>\n" + getHypononymsFooter(wordMatch);
                    InputTextMessageContent inputTextMessageContent = new InputTextMessageContent();
                    inputTextMessageContent.setMessageText(text);
                    inputTextMessageContent.setParseMode(ParseMode.HTML);
                    inputTextMessageContent.disableWebPagePreview();
                    InlineQueryResultArticle article = new InlineQueryResultArticle();
                    article.setId(id);
                    article.setTitle(title);
                    article.setInputMessageContent(inputTextMessageContent);
                    inlineQueryResults.add(article);
                }
            } else {
                InlineQueryResultArticle inlineQueryResult = new InlineQueryResultArticle();
                inlineQueryResult.setId("notfound");
                inlineQueryResult.setTitle("No Results Found!");
                InputTextMessageContent inputTextMessageContent = new InputTextMessageContent();
                inputTextMessageContent.setMessageText("Unable to find any results for <b>" + wordToFind + "</b>");
                inputTextMessageContent.setParseMode(ParseMode.HTML);
                inputTextMessageContent.disableWebPagePreview();
                inlineQueryResult.setInputMessageContent(inputTextMessageContent);
                inlineQueryResults.add(inlineQueryResult);
            }
            AnswerInlineQuery answerInlineQuery = new AnswerInlineQuery();
            answerInlineQuery.setInlineQueryId(query.getId());
            answerInlineQuery.setResults(inlineQueryResults);
            return answerInlineQuery;
        }
        return null;
    }

}
