# DictionBot
Telegram's [@DictionBot](https://telegram.me/dictionbot) - A simple English dictionary with almost every word in the English vocabulary

### Build Status ###
[![Build Status](https://travis-ci.org/shiblymeeran/dictionbot.svg)](https://travis-ci.org/shiblymeeran/dictionbot)

### Configuration for Bot Owners ###
Create a file named **dictionbot-config.json** and add the following:

```json
{
	"botApiToken": "YourBotApiTokenGoesHere",
	"commandList": ["/start","/help","/status","/scr"],
	"adminIdList": [1,2,3,4],
	"reportIntervalInSeconds": 86400
}
```
* **commandList** - The list of supported commands.
* **adminIdList** - Use [@GO_Robot](https://telegram.me/GO_Robot) to find your telegram ID and add it to admin list.
* **reportIntervalInSeconds** - The intervals at which the Bot reports the Admins the status (To know if it is alive). 

### Downloads [(Releases)](https://github.com/shiblymeeran/dictionbot/releases) ###
* DictionBot Executable JAR **(jar-with-dependencies)**

### Dependencies ###
* [easy-tbot](https://github.com/shiblymeeran/easy-tbot)
* RiTa WordNet Dictionary Library

### References ###
* [RiTa WordNet](https://rednoise.org/rita/reference/index.php)
