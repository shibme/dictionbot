package main

import (
	"log"
	"os"

	"github.com/shibme/dictionbot/dictionbot"
)

func main() {
	token := os.Getenv("TELEGRAM_BOT_TOKEN")
	if token == "" {
		log.Panicln("please set a valid token through TELEGEAM_BOT_TOKEN environment variable")
	}
	var dictionBot dictionbot.DictionBot
	err := dictionBot.Init(token)
	if err != nil {
		log.Fatalln(err.Error())
	}
	dictionBot.Start()
}
