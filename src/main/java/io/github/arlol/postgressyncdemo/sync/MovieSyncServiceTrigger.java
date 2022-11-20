package io.github.arlol.postgressyncdemo.sync;

import java.util.concurrent.Executor;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
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
		log.debug("trigger");
		syncExecutor.execute(() -> {
			while (service.sync().isPresent()) {

			}
		});
	}

}
