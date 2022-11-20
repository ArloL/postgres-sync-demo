package io.github.arlol.postgressyncdemo;

import java.time.Duration;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitAllStrategy;

@ContextConfiguration(initializers = MessagingDatabaseTest.Initializer.class)
@ActiveProfiles("rabbitmq")
public abstract class MessagingDatabaseTest extends DatabaseTest {

	public static class Initializer implements
			ApplicationContextInitializer<ConfigurableApplicationContext> {

		@Override
		public void initialize(
				ConfigurableApplicationContext applicationContext
		) {
			@SuppressWarnings("resource")
			RabbitMQContainer broker = new RabbitMQContainer(
					"rabbitmq:3.11.3-management-alpine"
			).withExposedPorts(5672, 15672)
					.waitingFor(
							new WaitAllStrategy()
									.withStrategy(Wait.forListeningPort())
									.withStrategy(
											Wait.forLogMessage(
													".*Server startup complete.*",
													1
											)
									)
									.withStartupTimeout(Duration.ofSeconds(60))
					);
			broker.start();

			applicationContext
					.addApplicationListener((ContextClosedEvent event) -> {
						broker.stop();
					});

			TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
					applicationContext,
					"spring.rabbitmq.host=" + broker.getHost(),
					"spring.rabbitmq.port=" + broker.getAmqpPort()
			);
		}

	}

}
