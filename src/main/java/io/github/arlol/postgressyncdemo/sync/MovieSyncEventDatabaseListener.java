package io.github.arlol.postgressyncdemo.sync;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.r2dbc.postgresql.api.PostgresqlConnection;
import io.r2dbc.postgresql.api.PostgresqlResult;
import io.r2dbc.spi.ConnectionFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class MovieSyncEventDatabaseListener
		implements AutoCloseable, InitializingBean {

	private Disposable subscription;
	private final ConnectionFactory connectionFactory;
	private final MovieSyncServiceTrigger trigger;
	private final Boolean enabled;
	@Setter
	@Getter
	private boolean listening;

	public MovieSyncEventDatabaseListener(
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
	public void close() {
		if (subscription != null) {
			subscription.dispose();
		}
		log.debug("close");
	}

	@Override
	public void afterPropertiesSet() {
		if (enabled) {
			start();
		}
	}

	public void start() {
		log.debug("start");
		subscription = Mono.from(connectionFactory.create())
				.flatMapMany(connection -> {
					if (!(connection instanceof PostgresqlConnection pgConnection)) {
						return connection.close();
					}
					return pgConnection
							.createStatement("LISTEN movie_sync_event_channel")
							.execute()
							.flatMap(PostgresqlResult::getRowsUpdated)
							.then(Mono.fromRunnable(() -> setListening(true)))
							.thenMany(pgConnection.getNotifications())
							.doOnNext(notification -> {
								log.debug(
										"Got notification: {}",
										notification.getName()
								);
								trigger.trigger();
							});
				})
				.doOnTerminate(() -> setListening(false))
				.subscribe();
	}

}
