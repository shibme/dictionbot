package main

import (
	"log"
	"os"

	"github.com/shibme/dictionbot/dictionbot"
)

func main() {
	token := os.Getenv("TELEGRAM_BOT_TOKEN")
	var dictionBot dictionbot.DictionBot
	err := dictionBot.Init(token)
	if err != nil {
		log.Fatalln(err.Error())
	}
	dictionBot.Start()
}
