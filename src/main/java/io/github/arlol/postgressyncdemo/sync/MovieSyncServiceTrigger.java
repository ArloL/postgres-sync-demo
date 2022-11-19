package io.github.arlol.postgressyncdemo.sync;

import java.util.concurrent.Executor;

import org.springframework.stereotype.Service;

@Service
public class MovieSyncServiceTrigger {

	private MovieSyncService service;
	private Executor syncExecutor;

	public MovieSyncServiceTrigger(
			Executor syncExecutor,
			MovieSyncService service
	) {
		this.syncExecutor = syncExecutor;
		this.service = service;
	}

	public void trigger() {
		syncExecutor.execute(() -> {
			while (service.sync().isPresent()) {

			}
		});
	}

}
