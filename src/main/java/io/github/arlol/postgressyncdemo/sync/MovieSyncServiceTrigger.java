package io.github.arlol.postgressyncdemo.sync;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class MovieSyncServiceTrigger {

	private MovieSyncService service;

	public MovieSyncServiceTrigger(MovieSyncService service) {
		this.service = service;
	}

	@Async
	public void trigger() {
		doSync();
	}

	@Scheduled(fixedDelay = 60_000)
	public void scheduledTrigger() {
		doSync();
	}

	private void doSync() {
		while (service.sync().isPresent()) {

		}
	}

}
