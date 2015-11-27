package me.shib.java.app.telegram.bot.dictionbot;

import java.io.IOException;
import java.util.Random;

import me.shib.java.lib.dictionary.service.DictionService;
import me.shib.java.lib.dictionary.service.DictionWord;
import me.shib.java.lib.telegram.bot.easybot.TBotModel;
import me.shib.java.lib.telegram.bot.service.TelegramBotService;
import me.shib.java.lib.telegram.bot.service.TelegramBotService.ChatAction;
import me.shib.java.lib.telegram.bot.types.ChatId;
import me.shib.java.lib.telegram.bot.types.Message;
import me.shib.java.lib.telegram.bot.types.ParseMode;
import me.shib.java.lib.telegram.bot.types.User;

public class DictionBotModel implements TBotModel {
	
	private static final String[] noResult = {"Sorry xxxxxxxxxx, looks like I have a lot to learn.",
			"Please accept my apology, xxxxxxxxxx. I don't know what that means.",
			"I couldn't find the synonym for that word in my book.",
			"I am still updating my knowledge xxxxxxxxxx. Will learn more soon. But I'm very sorry for now.",
			"Sorry xxxxxxxxxx, I couldn't figure it out.",
			"If that's really an english word I should have found it by now. I guess I'm not that good enough."};
	
	private static final String[] developerName = {"Shibly", "Meeran"};
	
	private DictionService dictionService;
	
	public DictionBotModel() {
		dictionService = new DictionService();
	}

	public Message onCommand(TelegramBotService tbs, Message msg) {
		String text = msg.getText();
		if(text != null) {
			if(text.equalsIgnoreCase("/start") || text.equalsIgnoreCase("/help")) {
				try {
					tbs.sendChatAction(new ChatId(msg.getChat().getId()), ChatAction.typing);
					return tbs.sendMessage(new ChatId(msg.getChat().getId()), "Hi *" + msg.getFrom().getFirst_name()	+ "*. My name is *Diction Bot* (DictionBot)."
							+ " Just type in any *English word* and I'll try to give you the best possible definition/description.\n"
							+ "Please give me the best possible rating here - [Click here to rate & review DictionBot](https://telegram.me/storebot?start=dictionbot)", ParseMode.Markdown);
				} catch (IOException e) {
					return null;
				}
			}
		}
		return null;
	}

	public Message onMessageFromAdmin(TelegramBotService tbs, Message msg) {
		return null;
	}
	
	private String getNoResultMessage(String name) {
		Random rand = new Random();
		return noResult[rand.nextInt(noResult.length)].replace("xxxxxxxxxx", "*" + name + "*");
	}
	
	private String getKnownUserMessage(String firstName, String lastName, String text) {
		for(String devName : developerName) {
			if(text.equalsIgnoreCase(devName)) {
				return "That guy gave me life. He doesn't want me to say anything more.";
			}
		}
		if(text.equalsIgnoreCase(firstName) || text.equalsIgnoreCase(lastName)) {
			return "Don't you know who you are?";
		}
		return null;
	}
	
	public Message onReceivingMessage(TelegramBotService tbs, Message msg) {
		try {
			String text = msg.getText();
			long sender = msg.getChat().getId();
			User sendingUser = msg.getFrom();
			if((text == null) || (text.split("\\s+").length > 1)) {
				return tbs.sendMessage(new ChatId(sender), "*" + sendingUser.getFirst_name() + "*, Please send only a single word as text. Let's play fair.", ParseMode.Markdown, false, msg.getMessage_id());
			}
			else {
				String knownUserMessage = getKnownUserMessage(sendingUser.getFirst_name(), sendingUser.getLast_name(), text);
				if(knownUserMessage != null) {
					return tbs.sendMessage(new ChatId(sender), knownUserMessage, ParseMode.None, false, msg.getMessage_id());
				}
				else {
					DictionWord descr = dictionService.getDictionWord(text);
					if(descr != null) {
						return tbs.sendMessage(new ChatId(sender), descr.toString(), ParseMode.None, false, msg.getMessage_id());
					}
					else {
						return tbs.sendMessage(new ChatId(sender), getNoResultMessage(sendingUser.getFirst_name()), ParseMode.Markdown, false, msg.getMessage_id());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Message sendStatusMessage(TelegramBotService tBotService, long chatId) {
		// TODO Auto-generated method stub
		return null;
	}

}
