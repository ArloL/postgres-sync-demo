package io.github.arlol.postgressyncdemo.sync;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.concurrent.ScheduledFuture;

import javax.sql.DataSource;

import org.postgresql.PGNotification;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MovieSyncEventPostgresJdbcNotificationListener
		implements AutoCloseable, InitializingBean {

	private final DataSource dataSource;
	private final TaskScheduler scheduler;

	private ScheduledFuture<?> scheduledFuture;
	private Connection connection;

	public MovieSyncEventPostgresJdbcNotificationListener(
			DataSourceProperties properties,
			TaskScheduler scheduler
	) {
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
		start();
	}

	public void start() throws Exception {
		connection = dataSource.getConnection();

		if (!org.postgresql.PGConnection.class
				.isAssignableFrom(connection.getClass())) {
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

	private void checkForNotifications() {
		try {
			var pgConnection = connection
					.unwrap(org.postgresql.PGConnection.class);
			var notifications = pgConnection.getNotifications();
			if (notifications != null) {
				for (PGNotification notification : notifications) {
					log.info("Got notification: {}", notification.getName());
				}
			}
		} catch (SQLException e) {
			log.error("Exception getting notifications", e);
		}
	}

}
