package io.github.arlol.postgressyncdemo.sync;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MovieSyncServiceTriggerScheduler {

	private final MovieSyncServiceTrigger trigger;

	public MovieSyncServiceTriggerScheduler(MovieSyncServiceTrigger trigger) {
		this.trigger = trigger;
	}

	@Scheduled(fixedDelay = 60_000)
	public void scheduledTrigger() {
		log.debug("scheduled trigger");
		trigger.trigger();
	}

}
