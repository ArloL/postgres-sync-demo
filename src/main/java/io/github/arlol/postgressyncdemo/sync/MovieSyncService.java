package io.github.arlol.postgressyncdemo.sync;

import java.util.Optional;
import java.util.function.Consumer;

import org.springframework.transaction.annotation.Transactional;

import io.github.arlol.postgressyncdemo.movie.Movie;
import io.github.arlol.postgressyncdemo.movie.MovieRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MovieSyncService {

	private final MovieSyncEventRepository repository;
	private final MovieRepository movieRepository;
	private final Consumer<MovieSyncEvent> movieSyncEventProcessor;
	private boolean enabled;

	public MovieSyncService(
			MovieSyncEventRepository movieSyncEventRepository,
			MovieRepository movieRepository,
			Consumer<MovieSyncEvent> movieSyncEventProcessor,
			boolean enabled
	) {
		this.repository = movieSyncEventRepository;
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
		var nextSyncEvent = repository.findAndDeleteNextSyncEvent();
		if (nextSyncEvent.isEmpty()) {
			return Optional.empty();
		}
		MovieSyncEvent event = nextSyncEvent.get();
		switch (event.getAction()) {
		case "I":
			event = syncInsert(event);
			break;
		case "U":
			event = syncUpdate(event);
			break;
		case "D":
			event = syncDelete(event);
			break;
		default:
			throw new IllegalStateException(
					"Unknown action " + event.getAction() + " for movie "
							+ event.getMovieId()
			);
		}

		movieSyncEventProcessor.accept(event);

		return Optional.of(event);
	}

	private MovieSyncEvent syncInsert(MovieSyncEvent event) {
		Optional<Movie> movie = movieRepository.findById(event.getMovieId());
		if (movie.isEmpty()) {
			log.debug(
					"Should insert movie {} but was deleted",
					event.getMovieId()
			);
			return event.toBuilder()
					.action("ID")
					.movie(Movie.builder().id(event.getMovieId()).build())
					.build();
		} else {
			log.debug("Should insert movie {}", event.getMovieId());
			return event.toBuilder().movie(movie.get()).build();
		}
	}

	private MovieSyncEvent syncUpdate(MovieSyncEvent event) {
		Optional<Movie> movie = movieRepository.findById(event.getMovieId());
		if (movie.isEmpty()) {
			log.debug(
					"Should update movie {} but was deleted",
					event.getMovieId()
			);
			return event.toBuilder()
					.action("UD")
					.movie(Movie.builder().id(event.getMovieId()).build())
					.build();
		} else {
			log.debug("Should update movie {}", event.getMovieId());
			return event.toBuilder().movie(movie.get()).build();
		}
	}

	private MovieSyncEvent syncDelete(MovieSyncEvent event) {
		log.debug("Should delete movie {}", event.getMovieId());
		return event.toBuilder()
				.action("D")
				.movie(Movie.builder().id(event.getMovieId()).build())
				.build();
	}

}
