# DictionBot
[![Build Status](https://travis-ci.org/shibme/dictionbot.svg)](https://travis-ci.org/shibme/dictionbot)
[![Dependency Status](https://www.versioneye.com/user/projects/56adffd17e03c7003db6962a/badge.svg?style=flat)](https://www.versioneye.com/user/projects/56adffd17e03c7003db6962a)

Telegram's [@DictionBot](https://telegram.me/DictionBot) - A simple English dictionary with almost every word in the English vocabulary

### Configuration for Bot Owners
Create a file named `jbots-config.json` and add the following
```json
[
	{
		"botApiToken": "YourBotApiTokenGoesHere",
		"botModelClassName": "me.shib.java.app.telegram.bot.dictionbot.DictionBot",
		"commandList": ["/start","/help","/status","/usermode","/adminmode"],
		"threadCount": 4,
		"adminIdList": [0, 0],
		"reportIntervalInSeconds": 43200,
		"botStatsConfig": {
			"botStatsClassName": "me.shib.java.lib.jbotan.JBotan",
			"token": "BotanTokenGoesHere"
		}
	}
]
```
* `botApiToken` - The API token that you receive when you create a bot with [@BotFather](https://telegram.me/BotFather).
* `botModelClassName` - The fully qualified class name of the bot (You don't have to change what's given above).
* `commandList` - The list of supported commands.
* `threadCount` - The number of threads the bot should have. This bot is restricted to 7 threads.
* `adminIdList` - Use [@GO_Robot](https://telegram.me/GO_Robot) to find your telegram ID and add it to admin list.
* `reportIntervalInSeconds` - The intervals at which the Bot reports the Admins the status (To know if it is alive). 

### Downloads [(Releases)](https://github.com/shibme/dictionbot/releases)
* DictionBot Executable JAR `DictionBot.jar`