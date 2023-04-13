package main

import (
	"log"
	"os"

	"github.com/shibme/dictionbot/dictionbot"
)

var tokenEnvarName = "TELEGRAM_BOT_TOKEN"

func main() {
	var token string
	if len(os.Args) > 1 {
		token = os.Args[1]
	}
	if token == "" {
		token = os.Getenv(tokenEnvarName)
	}
	if token == "" {
		log.Panicln("please set a valid token through " + tokenEnvarName + " environment variable")
	}
	var dictionBot dictionbot.DictionBot
	err := dictionBot.Init(token)
	if err != nil {
		log.Fatalln(err.Error())
	}
	dictionBot.Start()
}
