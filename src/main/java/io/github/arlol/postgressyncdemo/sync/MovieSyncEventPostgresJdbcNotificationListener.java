package io.github.arlol.postgressyncdemo.sync;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.concurrent.ScheduledFuture;

import javax.sql.DataSource;

import org.postgresql.PGNotification;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.scheduling.TaskScheduler;

import lombok.extern.slf4j.Slf4j;

// Disable it for now
// @Service
@Slf4j
public class MovieSyncEventPostgresJdbcNotificationListener
		implements AutoCloseable, InitializingBean {

	private final DataSource dataSource;
	private final TaskScheduler scheduler;
	private final MovieSyncServiceTrigger trigger;
	private final boolean enabled;

	private ScheduledFuture<?> scheduledFuture;
	private Connection connection;

	public MovieSyncEventPostgresJdbcNotificationListener(
			DataSourceProperties properties,
			TaskScheduler scheduler,
			MovieSyncServiceTrigger trigger,
			@Value(
				"${postgres-sync-demo.movie-sync-service.enabled:true}"
			) boolean enabled
	) {
		this.trigger = trigger;
		this.enabled = enabled;
		this.dataSource = properties.initializeDataSourceBuilder()
				.type(SimpleDriverDataSource.class)
				.build();
		this.scheduler = scheduler;
	}

	@Override
	public void close() throws Exception {
		if (scheduledFuture != null) {
			scheduledFuture.cancel(true);
		}
		if (connection != null) {
			connection.close();
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (enabled) {
			start();
		}
	}

	public void start() throws Exception {
		connection = dataSource.getConnection();

		if (!isPgConnection(connection)) {
			return;
		}

		try (Statement statement = connection.createStatement()) {
			statement.execute("LISTEN movie_sync_event_channel");
		}

		scheduledFuture = scheduler.scheduleWithFixedDelay(
				this::checkForNotifications,
				Duration.ofMillis(150)
		);
	}

	private boolean isPgConnection(Connection connection) {
		try {
			connection.unwrap(org.postgresql.PGConnection.class);
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

	private void checkForNotifications() {
		try {
			var pgConnection = connection
					.unwrap(org.postgresql.PGConnection.class);
			var notifications = pgConnection.getNotifications();
			if (notifications != null) {
				for (PGNotification notification : notifications) {
					log.info("Got notification: {}", notification.getName());
					trigger.trigger();
				}
			}
		} catch (SQLException e) {
			log.error("Exception getting notifications", e);
		}
	}

}
