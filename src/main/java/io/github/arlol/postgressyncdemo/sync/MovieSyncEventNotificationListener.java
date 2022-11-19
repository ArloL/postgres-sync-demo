package io.github.arlol.postgressyncdemo.sync;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.r2dbc.postgresql.api.PostgresqlConnection;
import io.r2dbc.postgresql.api.PostgresqlResult;
import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class MovieSyncEventNotificationListener
		implements AutoCloseable, InitializingBean {

	private Disposable subscription;
	private ConnectionFactory connectionFactory;
	private MovieSyncServiceTrigger trigger;
	private Boolean enabled;

	public MovieSyncEventNotificationListener(
			ConnectionFactory connectionFactory,
			MovieSyncServiceTrigger trigger,
			@Value(
				"${postgres-sync-demo.movie-sync-service.enabled:true}"
			) boolean enabled
	) {
		this.connectionFactory = connectionFactory;
		this.trigger = trigger;
		this.enabled = enabled;
	}

	@Override
	public void close() throws Exception {
		if (subscription != null) {
			subscription.dispose();
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (enabled) {
			start();
		}
	}

	public void start() throws Exception {
		subscription = Mono.from(connectionFactory.create())
				.flatMapMany(connection -> {
					if (!(connection instanceof PostgresqlConnection)) {
						return connection.close();
					}
					PostgresqlConnection pgConnection = (PostgresqlConnection) connection;
					return pgConnection
							.createStatement("LISTEN movie_sync_event_channel")
							.execute()
							.flatMap(PostgresqlResult::getRowsUpdated)
							.thenMany(pgConnection.getNotifications())
							.doOnNext(notification -> {
								log.debug(
										"Got notification: {}",
										notification.getName()
								);
								trigger.trigger();
							});
				})
				.subscribe();
	}

}
