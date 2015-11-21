package me.shib.java.app.telegram.bot.dictionbot;

import java.io.File;

import me.shib.java.lib.telegram.bot.easybot.TBotConfig;
import me.shib.java.lib.telegram.bot.easybot.TBotWorker;

public class StartDictionBot {
	
	private static final int threadCount = 7;
	
	public static void main(String[] args) throws InterruptedException {
		TBotConfig tbConfig = TBotConfig.getFileConfig(new File("DictionBotConfig.json"));
		TBotWorker[] botWorkers = new TBotWorker[threadCount];
		for(int i = 0; i < threadCount; i++) {
			botWorkers[i] = new TBotWorker(new DictionBotModel(), tbConfig);
			botWorkers[i].start();
		}
		boolean threadAlive = true;
		while(threadAlive) {
			for(int i = 0; i < threadCount; i++) {
				if(!botWorkers[i].isAlive()) {
					threadAlive = false;
				}
			}
			Thread.sleep(4444);
		}
	}
}