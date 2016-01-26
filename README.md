# DictionBot
Telegram's [@DictionBot](https://telegram.me/dictionbot) - A simple English dictionary with almost every word in the English vocabulary

### Build Status ###
[![Build Status](https://travis-ci.org/shibme/dictionbot.svg)](https://travis-ci.org/shibme/dictionbot)

### Configuration for Bot Owners ###
Create a file named **jbots-config.json** and add the following:

```json
[
	{
		"botApiToken": "YourBotApiTokenGoesHere",
		"botModelclassName": "me.shib.java.app.telegram.bot.dictionbot.DictionBotModel",
		"commandList": ["/start","/help","/status","/scr"],
		"threadCount": 4,
		"adminIdList": [0, 0],
		"reportIntervalInSeconds": 86400
	}
]
```
* **botLauncherclassName** - The fully qualified class name of the bot (You don't have to change what's given above).
* **botApiToken** - The API token that you receive when you create a bot with [@BotFather](https://telegram.me/BotFather).
* **commandList** - The list of supported commands.
* **threadCount** - The number of threads the bot should have. This bot is restricted to 7 threads.
* **adminIdList** - Use [@GO_Robot](https://telegram.me/GO_Robot) to find your telegram ID and add it to admin list.
* **reportIntervalInSeconds** - The intervals at which the Bot reports the Admins the status (To know if it is alive). 

### Downloads [(Releases)](https://github.com/shibme/dictionbot/releases) ###
* DictionBot Executable JAR **(DictionBot.jar)**

### References ###
* [RiTa WordNet](https://rednoise.org/rita/reference/index.php)
