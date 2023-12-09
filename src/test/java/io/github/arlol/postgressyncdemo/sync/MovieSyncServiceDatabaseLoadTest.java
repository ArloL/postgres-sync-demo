package io.github.arlol.postgressyncdemo.sync;

import org.junit.jupiter.api.AfterEach;
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
public class MovieSyncServiceDatabaseLoadTest extends DatabaseTest {

	@Autowired
	MovieSyncEventDatabaseListener listener;
	@Autowired
	ControllableMovieSyncService movieSyncService;

	LoadTest loadTest;

	@BeforeEach
	public void beforeEach() throws Exception {
		loadTest = new LoadTest(
				log,
				listener,
				movieSyncService,
				movieRepository,
				movieSyncEventRepository,
				watchListRepository
		);
		loadTest.beforeEach();
	}

	@AfterEach
	public void afterEach() throws Exception {
		loadTest.afterEach();
	}

	@Test
	void test() throws Exception {
		loadTest.test();
	}

}
