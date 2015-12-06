# DictionBot
Telegram's [@DictionBot](https://telegram.me/dictionbot) - A simple English dictionary with almost every word in the English vocabulary

### Build Status ###
[![Build Status](https://travis-ci.org/shiblymeeran/dictionbot.svg)](https://travis-ci.org/shiblymeeran/dictionbot)

### Configuration for Bot Owners ###
Create a file named **easy-bot-config.json** and add the following:

```json
[
	{
		"botLauncherclassName": "me.shib.java.app.telegram.bot.dictionbot.DictionBotLauncher",
		"botApiToken": "YourBotApiTokenGoesHere",
		"commandList": ["/start","/help","/status","/scr"],
		"threadCount": 4,
		"adminIdList": [000000000, 000000000],
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

### Downloads [(Releases)](https://github.com/shiblymeeran/dictionbot/releases) ###
* DictionBot Executable JAR **(DictionBot.jar)**

### Dependencies ###
* [easy-tbot](https://github.com/shiblymeeran/easy-tbot)
* RiTa WordNet Dictionary Library

### References ###
* [RiTa WordNet](https://rednoise.org/rita/reference/index.php)
