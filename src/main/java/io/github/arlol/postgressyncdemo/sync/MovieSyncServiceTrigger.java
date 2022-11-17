package io.github.arlol.postgressyncdemo.sync;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MovieSyncServiceTrigger {

	private MovieSyncService service;

	public MovieSyncServiceTrigger(MovieSyncService service) {
		this.service = service;
	}

	@Async
	public void trigger() {
		while (service.sync().isPresent()) {

		}
	}

}
