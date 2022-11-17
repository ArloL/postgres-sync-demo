package io.github.arlol.postgressyncdemo.sync;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class MovieSyncServiceTriggerScheduler {

	private MovieSyncServiceTrigger trigger;

	public MovieSyncServiceTriggerScheduler(MovieSyncServiceTrigger trigger) {
		this.trigger = trigger;
	}

	@Scheduled(fixedDelay = 60_000)
	public void scheduledTrigger() {
		trigger.trigger();
	}

}
