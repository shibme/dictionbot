package me.shib.java.telegram.bot.dictionbot;

import java.io.File;

import me.shib.java.telegram.easybot.framework.TBotConfig;
import me.shib.java.telegram.easybot.framework.TBotWorker;

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
