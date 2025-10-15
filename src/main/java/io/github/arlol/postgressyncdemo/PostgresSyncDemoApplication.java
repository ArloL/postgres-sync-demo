package io.github.arlol.postgressyncdemo;

import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import io.github.arlol.postgressyncdemo.movie.MovieRepository;
import io.github.arlol.postgressyncdemo.sync.MovieSyncEvent;
import io.github.arlol.postgressyncdemo.sync.MovieSyncEventRepository;
import io.github.arlol.postgressyncdemo.sync.MovieSyncEventToDatabase;
import io.github.arlol.postgressyncdemo.sync.MovieSyncEventToRabbit;
import io.github.arlol.postgressyncdemo.sync.MovieSyncService;

@SpringBootApplication(exclude = RabbitAutoConfiguration.class)
@EnableScheduling
@EnableAsync
// Explicit import due to r2dbc and jdbc being on the classpath
@Import(DataSourceAutoConfiguration.class)
public class PostgresSyncDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(PostgresSyncDemoApplication.class, args);
	}

	@Bean
	Executor syncExecutor() {
		return new ThreadPoolExecutor(
				1,
				1,
				0L,
				TimeUnit.MILLISECONDS,
				new LinkedBlockingDeque<>(1),
				new ThreadPoolExecutor.DiscardPolicy()
		);
	}

	@Bean
	MovieSyncService movieSyncService(
			MovieSyncEventRepository movieSyncEventRepository,
			MovieRepository movieRepository,
			MovieSyncEventToDatabase movieSyncEventToDatabase,
			Optional<MovieSyncEventToRabbit> movieSyncEventToRabbit,
			@Value(
				"${postgres-sync-demo.movie-sync-service.enabled:true}"
			) boolean enabled

	) {
		Consumer<MovieSyncEvent> movieSyncEventProcessor = movieSyncEventToDatabase;
		if (movieSyncEventToRabbit.isPresent()) {
			movieSyncEventProcessor = movieSyncEventToRabbit.orElseThrow();
		}
		return new MovieSyncService(
				movieSyncEventRepository,
				movieRepository,
				movieSyncEventProcessor,
				enabled
		);
	}

}
