package io.github.arlol.postgressyncdemo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitAllStrategy;
import org.testcontainers.junit.jupiter.Testcontainers;

import io.github.arlol.postgressyncdemo.movie.MovieRepository;
import io.github.arlol.postgressyncdemo.sync.ControllableMovieSyncServiceConfiguration;
import io.github.arlol.postgressyncdemo.sync.MovieSyncEventRepository;
import io.github.arlol.postgressyncdemo.watchlist.WatchListRepository;

@Testcontainers(disabledWithoutDocker = true)
@ContextConfiguration(initializers = DatabaseTest.DataSourceInitializer.class)
@ActiveProfiles({ "default", "postgres" })
@SpringBootTest(classes = { ControllableMovieSyncServiceConfiguration.class })
public abstract class DatabaseTest {

	public static class DataSourceInitializer implements
			ApplicationContextInitializer<ConfigurableApplicationContext> {

		@Override
		public void initialize(
				ConfigurableApplicationContext applicationContext
		) {
			@SuppressWarnings("resource")
			PostgreSQLContainer<?> database = new PostgreSQLContainer<>(
					"postgres:18.0-trixie"
			).waitingFor(
					new WaitAllStrategy().withStrategy(Wait.forListeningPort())
							.withStrategy(
									Wait.forLogMessage(
											".*database system is ready to accept connections.*\\s",
											2
									)
							)
			);
			database.start();

			applicationContext
					.addApplicationListener((ContextClosedEvent event) -> {
						database.stop();
					});

			TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
					applicationContext,
					"spring.test.database.replace=none",
					"spring.sql.init.mode=always",
					"spring.datasource.url=" + database.getJdbcUrl(),
					"spring.datasource.username=" + database.getUsername(),
					"spring.datasource.password=" + database.getPassword(),
					"spring.r2dbc.url="
							+ database.getJdbcUrl().replace("jdbc", "r2dbc")
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
	public void checkForEmptyDatabase() {
		assertThat(movieRepository.findAll()).isEmpty();
		assertThat(movieSyncEventRepository.findAll()).isEmpty();
		assertThat(watchListRepository.findAll()).isEmpty();
	}

}
