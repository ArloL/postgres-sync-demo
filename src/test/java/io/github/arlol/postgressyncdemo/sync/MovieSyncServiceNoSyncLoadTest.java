package io.github.arlol.postgressyncdemo.sync;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import io.github.arlol.postgressyncdemo.DatabaseTest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag("load")
@ActiveProfiles("postgres-with-trigger")
public class MovieSyncServiceNoSyncLoadTest extends DatabaseTest {

	@Autowired
	MovieSyncEventDatabaseListener listener;
	@Autowired
	ControllableMovieSyncService movieSyncService;

	LoadScenario loadScenario;

	@BeforeEach
	public void beforeEach() throws Exception {
		loadScenario = new LoadScenario(
				log,
				listener,
				movieSyncService,
				movieRepository,
				movieSyncEventRepository,
				watchListRepository
		);
	}

	@Test
	void test() throws Exception {
		loadScenario.test(0);
	}

}
