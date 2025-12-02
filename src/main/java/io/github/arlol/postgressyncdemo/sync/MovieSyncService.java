package io.github.arlol.postgressyncdemo.sync;

import java.util.Optional;
import java.util.function.Consumer;

import org.springframework.transaction.annotation.Transactional;

import io.github.arlol.postgressyncdemo.movie.Movie;
import io.github.arlol.postgressyncdemo.movie.MovieRepository;
import lombok.Getter;

public class MovieSyncService {

	private final MovieSyncEventRepository movieSyncEventRepository;
	private final MovieRepository movieRepository;
	private final Consumer<MovieSyncEvent> movieSyncEventProcessor;
	@Getter
	private final boolean enabled;

	public MovieSyncService(
			MovieSyncEventRepository movieSyncEventRepository,
			MovieRepository movieRepository,
			Consumer<MovieSyncEvent> movieSyncEventProcessor,
			boolean enabled
	) {
		this.movieSyncEventRepository = movieSyncEventRepository;
		this.movieRepository = movieRepository;
		this.movieSyncEventProcessor = movieSyncEventProcessor;
		this.enabled = enabled;
	}

	@Transactional("transactionManager")
	public Optional<MovieSyncEvent> sync() {
		if (!isEnabled()) {
			return Optional.empty();
		}
		var syncEvent = movieSyncEventRepository.findAndDeleteNextSyncEvent()
				.map(this::process);
		syncEvent.ifPresent(movieSyncEventProcessor);
		return syncEvent;
	}

	private MovieSyncEvent process(MovieSyncEvent event) {
		if ("D".equals(event.action())) {
			return event;
		}
		Optional<Movie> movie = movieRepository.findById(event.movieId());
		if (movie.isEmpty()) {
			return event.toBuilder().action("D").build();
		}
		return event.toBuilder().movie(movie.orElseThrow()).build();
	}

}
