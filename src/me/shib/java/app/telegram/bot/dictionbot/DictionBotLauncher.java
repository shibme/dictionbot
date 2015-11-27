package me.shib.java.app.telegram.bot.dictionbot;

import me.shib.java.lib.telegram.bot.easybot.TBotConfig;
import me.shib.java.lib.telegram.bot.easybot.TBotWorker;
import me.shib.java.lib.telegram.bot.run.TBotLauncherModel;

public class DictionBotLauncher implements TBotLauncherModel {
	
	private static final int maxThreadLimit = 7;
	
	@Override
	public TBotWorker[] launchBot(TBotConfig tBotConfig) {
		int threadCount = tBotConfig.getThreadCount();
		if (threadCount > maxThreadLimit) {
			threadCount = maxThreadLimit;
		}
		TBotWorker[] botWorkers = new TBotWorker[threadCount];
		for (int i = 0; i < threadCount; i++) {
			botWorkers[i] = new TBotWorker(new DictionBotModel(), tBotConfig);
			botWorkers[i].start();
		}
		return botWorkers;
	}

}
