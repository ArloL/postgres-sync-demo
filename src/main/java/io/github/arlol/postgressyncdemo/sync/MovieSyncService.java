package io.github.arlol.postgressyncdemo.sync;

import java.util.Optional;
import java.util.function.Consumer;

import org.springframework.transaction.annotation.Transactional;

import io.github.arlol.postgressyncdemo.movie.Movie;
import io.github.arlol.postgressyncdemo.movie.MovieRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MovieSyncService {

	private final MovieSyncEventRepository movieSyncEventRepository;
	private final MovieRepository movieRepository;
	private final Consumer<MovieSyncEvent> movieSyncEventProcessor;
	private boolean enabled;

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

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Transactional
	public Optional<MovieSyncEvent> sync() {
		if (!enabled) {
			return Optional.empty();
		}
		var syncEvent = movieSyncEventRepository.findAndDeleteNextSyncEvent()
			.map(this::process);
		syncEvent.ifPresent(movieSyncEventProcessor::accept);
		return syncEvent;
	}

	private MovieSyncEvent process(MovieSyncEvent event) {
		if ("D".equals(event.getAction())) {
			return event;
		}
		Optional<Movie> movie = movieRepository.findById(event.getMovieId());
		if (movie.isEmpty()) {
			return event.toBuilder().action("D").build();
		}
		return event.toBuilder().movie(movie.orElseThrow()).build();
	}

}
