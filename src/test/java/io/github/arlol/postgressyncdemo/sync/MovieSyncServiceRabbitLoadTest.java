package io.github.arlol.postgressyncdemo.sync;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.github.arlol.postgressyncdemo.MessagingDatabaseTest;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class MovieSyncServiceRabbitLoadTest extends MessagingDatabaseTest {

	@Autowired
	MovieSyncEventDatabaseListener listener;
	@Autowired
	MovieSyncService movieSyncService;

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
