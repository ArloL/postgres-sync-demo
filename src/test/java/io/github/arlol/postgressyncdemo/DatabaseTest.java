package io.github.arlol.postgressyncdemo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitAllStrategy;
import org.testcontainers.junit.jupiter.Testcontainers;

import io.github.arlol.postgressyncdemo.movie.MovieRepository;
import io.github.arlol.postgressyncdemo.sync.MovieSyncEventRepository;
import io.github.arlol.postgressyncdemo.watchlist.WatchListRepository;

@Testcontainers(disabledWithoutDocker = true)
@ContextConfiguration(initializers = DatabaseTest.DataSourceInitializer.class)
public abstract class DatabaseTest {

	private static final PostgreSQLContainer<?> DATABASE = new PostgreSQLContainer<>(
			"postgres:15.1-alpine"
	).waitingFor(
			new WaitAllStrategy().withStrategy(Wait.forListeningPort())
					.withStrategy(
							Wait.forLogMessage(
									".*database system is ready to accept connections.*\\s",
									2
							)
					)
	);

	public static class DataSourceInitializer implements
			ApplicationContextInitializer<ConfigurableApplicationContext> {

		@Override
		public void initialize(
				ConfigurableApplicationContext applicationContext
		) {
			applicationContext
					.addApplicationListener((ContextClosedEvent event) -> {
						DATABASE.stop();
					});

			if (!DATABASE.isRunning()) {
				DATABASE.start();
			}

			TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
					applicationContext,
					"spring.test.database.replace=none",
					"spring.sql.init.platform=postgres",
					"spring.sql.init.mode=always",
					"spring.datasource.url=" + DATABASE.getJdbcUrl(),
					"spring.datasource.username=" + DATABASE.getUsername(),
					"spring.datasource.password=" + DATABASE.getPassword(),
					"spring.r2dbc.url="
							+ DATABASE.getJdbcUrl().replace("jdbc", "r2dbc")
			);
		}

	}

	@Autowired
	public MovieRepository movieRepository;
	@Autowired
	public MovieSyncEventRepository movieSyncEventRepository;
	@Autowired
	public WatchListRepository watchListRepository;

	@AfterEach
	public void afterEach() {
		assertThat(movieRepository.findAll(Pageable.ofSize(1))).isEmpty();
		assertThat(movieSyncEventRepository.findAll(Pageable.ofSize(1)))
				.isEmpty();
		assertThat(watchListRepository.findAll(Pageable.ofSize(1))).isEmpty();
	}

}
