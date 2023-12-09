package io.github.arlol.postgressyncdemo.sync;

import java.util.Optional;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import io.github.arlol.postgressyncdemo.movie.MovieRepository;

@TestConfiguration
public class ControllableMovieSyncServiceConfiguration {

	@Bean
	@Primary
	ControllableMovieSyncService movieSyncService(
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
		return new ControllableMovieSyncService(
				movieSyncEventRepository,
				movieRepository,
				movieSyncEventProcessor,
				enabled
		);
	}

}
