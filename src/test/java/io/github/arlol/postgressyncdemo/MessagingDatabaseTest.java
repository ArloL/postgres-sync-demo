package io.github.arlol.postgressyncdemo;

import java.time.Duration;

import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitAllStrategy;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers(disabledWithoutDocker = true)
@ContextConfiguration(initializers = MessagingDatabaseTest.Initializer.class)
@Import(RabbitAutoConfiguration.class)
public abstract class MessagingDatabaseTest {

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
					.withStartupTimeout(Duration.ofSeconds(60))
	);

	private static final RabbitMQContainer BROKER = new RabbitMQContainer(
			"rabbitmq:3.11.3-management-alpine"
	).withExposedPorts(5672, 15672)
			.waitingFor(
					new WaitAllStrategy().withStrategy(Wait.forListeningPort())
							.withStrategy(
									Wait.forLogMessage(
											".*Server startup complete.*",
											1
									)
							)
							.withStartupTimeout(Duration.ofSeconds(60))
			);

	public static class Initializer implements
			ApplicationContextInitializer<ConfigurableApplicationContext> {

		@Override
		public void initialize(
				ConfigurableApplicationContext applicationContext
		) {
			applicationContext
					.addApplicationListener((ContextClosedEvent event) -> {
						BROKER.stop();
						DATABASE.stop();
					});

			if (!BROKER.isRunning()) {
				BROKER.start();
			}
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
							+ DATABASE.getJdbcUrl().replace("jdbc", "r2dbc"),
//					"spring.rabbitmq.ssl.enabled=false",
					"spring.rabbitmq.host=" + BROKER.getHost(),
					"spring.rabbitmq.port=" + BROKER.getAmqpPort()
			);
		}

	}

}
