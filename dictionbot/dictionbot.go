package dictionbot

import (
	"encoding/json"
	"errors"
	"log"
	"strconv"
	"strings"

	telegramBot "github.com/go-telegram-bot-api/telegram-bot-api/v5"
)

type DictionBot struct {
	bot  *telegramBot.BotAPI
	self telegramBot.User
}

func (dictionBot *DictionBot) getIntroMessageText() (introMessage string) {
	return "Hi. My name is <b>@" + dictionBot.bot.Self.UserName + "</b>. " +
		"Just type in any <b>English</b> word and I'll try to explain what all it might mean."
}

func (dictionBot *DictionBot) Init(telegramBotApiToken string) (err error) {
	if telegramBotApiToken == "" {
		err = errors.New("Please provide a valid Bot API Token")
		return
	}
	dictionBot.bot, err = telegramBot.NewBotAPI(telegramBotApiToken)
	if err == nil {
		dictionBot.self = dictionBot.bot.Self
	}
	return
}

func toJSON(v any) string {
	json, err := json.Marshal(v)
	if err != nil {
		log.Fatalln("Something went wrong while logging the update")
	}
	return string(json)
}

func (dictionBot *DictionBot) Start() {
	updater := telegramBot.NewUpdate(0)
	updater.Timeout = 60
	updates := dictionBot.bot.GetUpdatesChan(updater)
	for update := range updates {
		log.Println(toJSON(update))
		if update.Message != nil {
			go dictionBot.onTextMessage(*update.Message)
		} else if update.InlineQuery != nil {
			go dictionBot.onInlineQuery(*update.InlineQuery)
		}
	}
}

func (dictionBot *DictionBot) sendTypingAction(chatID int64) {
	dictionBot.bot.Send(telegramBot.NewChatAction(chatID, telegramBot.ChatTyping))
}

func (dictionBot *DictionBot) onTextMessage(message telegramBot.Message) {
	if message.Text == "" {
		msg := telegramBot.NewMessage(message.Chat.ID,
			"Please send a valid <b>text</b> message")
		msg.ReplyToMessageID = message.MessageID
		msg.ParseMode = telegramBot.ModeHTML
		dictionBot.bot.Send(msg)
	} else {
		dictionBot.processTextMessage(message)
	}
}

func (dictionBot *DictionBot) getRelatedWordsFooter(diction Diction) string {
	var relatedWordsFooter strings.Builder
	if (len(diction.hyponyms) + len(diction.hypernyms)) > 0 {
		relatedWordsFooter.WriteString("\n\n<b>Related words:</b>\n")
		var maxHyponyms, maxHypernyms int
		if (len(diction.hyponyms) + len(diction.hypernyms)) > maxRelatedWords {
			maxHyponyms = maxRelatedWords / 2
			maxHypernyms = maxRelatedWords / 2
			if len(diction.hyponyms) < maxHyponyms {
				maxHyponyms = len(diction.hyponyms)
				maxHypernyms = maxRelatedWords - maxHyponyms
			} else if len(diction.hypernyms) < maxHypernyms {
				maxHypernyms = len(diction.hypernyms)
				maxHyponyms = maxRelatedWords - maxHypernyms
			}
		} else {
			maxHyponyms = len(diction.hyponyms)
			maxHypernyms = len(diction.hypernyms)
		}
		var relatedWordsHTMLFormat []string
		for i := 0; i < maxHyponyms; i++ {
			relatedWordsHTMLFormat = append(relatedWordsHTMLFormat,
				"<i>"+dictionBot.getRefURL(diction.hyponyms[i], diction.hyponyms[i])+"</i>")
		}
		for i := 0; i < maxHypernyms; i++ {
			relatedWordsHTMLFormat = append(relatedWordsHTMLFormat,
				"<i>"+dictionBot.getRefURL(diction.hypernyms[i], diction.hypernyms[i])+"</i>")
		}
		relatedWordsFooter.WriteString(strings.Join(relatedWordsHTMLFormat, " - "))
	}
	return relatedWordsFooter.String()
}

func (dictionBot *DictionBot) processTextMessage(message telegramBot.Message) {
	dictionBot.sendTypingAction(message.Chat.ID)
	if message.IsCommand() {
		if message.CommandArguments() == "" {
			msg := telegramBot.NewMessage(message.Chat.ID, "")
			msg.ReplyToMessageID = message.MessageID
			msg.ParseMode = telegramBot.ModeHTML
			switch message.Command() {
			case "start":
				msg.Text = dictionBot.getIntroMessageText()
			case "help":
				msg.Text = dictionBot.getIntroMessageText()
			default:
				msg.Text = "Invalid command. Please use /start or /help to know about me."
			}
			dictionBot.bot.Send(msg)
		} else {
			dictionBot.processWords(message, message.CommandArguments())
		}
	} else {
		dictionBot.processWords(message, message.Text)
	}
}

func (dictionBot *DictionBot) processWords(message telegramBot.Message, text string) {
	dictions, limitExceeded := GetDictions(text)
	if limitExceeded {
		msg := telegramBot.NewMessage(message.Chat.ID,
			"Can't process more than <b>"+strconv.Itoa(maxWordsPerText)+"</b> words in a single message")
		msg.ReplyToMessageID = message.MessageID
		msg.ParseMode = telegramBot.ModeHTML
		dictionBot.bot.Send(msg)
	} else {
		for _, diction := range dictions {
			if diction.valid {
				if len(diction.descriptions) > 0 {
					var msgTextBuilder strings.Builder
					msgTextBuilder.WriteString("<b>" + diction.word + "</b>")
					for _, desc := range diction.descriptions {
						msgTextBuilder.WriteString("\n\n" + "<u>" + desc.pos + "</u>\t<code>" + desc.description + "</code>")
					}
					msgTextBuilder.WriteString(dictionBot.getRelatedWordsFooter(diction))
					msg := telegramBot.NewMessage(message.Chat.ID, msgTextBuilder.String())
					msg.ReplyToMessageID = message.MessageID
					msg.ParseMode = telegramBot.ModeHTML
					dictionBot.bot.Send(msg)
				} else {
					msg := telegramBot.NewMessage(message.Chat.ID,
						"No results found for <b>"+diction.word+"</b>")
					msg.ReplyToMessageID = message.MessageID
					msg.ParseMode = telegramBot.ModeHTML
					dictionBot.bot.Send(msg)
				}
			} else {
				msg := telegramBot.NewMessage(message.Chat.ID,
					"Please avoid looking up something invalid, such as [<b>"+diction.word+"</b>]")
				msg.ReplyToMessageID = message.MessageID
				msg.ParseMode = telegramBot.ModeHTML
				dictionBot.bot.Send(msg)
			}
		}
	}
}

func (dictionBot *DictionBot) getRefURL(word, hyperlinkText string) string {
	return "<a href=\"https://t.me/" + dictionBot.self.UserName + "?start=" + word + "\">" + hyperlinkText + "</a>"
}

func (dictionBot *DictionBot) onInlineQuery(inlineQuery telegramBot.InlineQuery) {
	text := inlineQuery.Query
	diction := GetDiction(text)
	inlineQueryResults := make([]interface{}, 0)
	for i, desc := range diction.descriptions {
		id := "dictionbot_desc_" + strconv.Itoa(i)
		title := desc.pos + " - " + desc.description
		text := "<b>" + dictionBot.getRefURL(diction.word, diction.word) + "</b> <i>[<u>" + desc.pos + "</u>]</i>\t<code>" +
			desc.description + "</code>\n\n<b>" + dictionBot.getRefURL(diction.word, "Show All") + "</b>"
		article := telegramBot.NewInlineQueryResultArticleHTML(id, title, text)
		inlineQueryResults = append(inlineQueryResults, article)
	}
	if len(inlineQueryResults) == 0 {
		id := "no_result"
		title := "No Results Found!"
		text := "No results found for <b>" + text + "</b>"
		article := telegramBot.NewInlineQueryResultArticleHTML(id, title, text)
		inlineQueryResults = append(inlineQueryResults, article)
	}
	answer := telegramBot.InlineConfig{
		InlineQueryID: inlineQuery.ID,
		Results:       inlineQueryResults,
	}
	if _, err := dictionBot.bot.Send(answer); err != nil {
		log.Println(err)
	}
}
