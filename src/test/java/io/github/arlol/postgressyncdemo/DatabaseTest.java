package io.github.arlol.postgressyncdemo;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitAllStrategy;

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

}
