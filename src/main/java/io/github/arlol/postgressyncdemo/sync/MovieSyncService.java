package io.github.arlol.postgressyncdemo.sync;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.arlol.postgressyncdemo.movie.Movie;
import io.github.arlol.postgressyncdemo.movie.MovieRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MovieSyncService {

	private final MovieSyncEventRepository repository;
	private final MovieRepository movieRepository;
	private final boolean enabled;

	public MovieSyncService(
			MovieSyncEventRepository movieSyncEventRepository,
			MovieRepository movieRepository,
			@Value(
				"${postgres-sync-demo.movie-sync-service.enabled:true}"
			) boolean enabled
	) {
		this.repository = movieSyncEventRepository;
		this.movieRepository = movieRepository;
		this.enabled = enabled;
	}

	@Transactional
	public Optional<MovieSyncResult> sync() {
		if (!enabled) {
			return Optional.empty();
		}
		var nextSyncEvent = repository.findAndDeleteNextSyncEvent();
		if (nextSyncEvent.isEmpty()) {
			return Optional.empty();
		}
		MovieSyncEvent event = nextSyncEvent.get();
		String action;
		switch (event.getAction()) {
		case "I":
			action = syncInsert(event.getMovieId());
			break;
		case "U":
			action = syncUpdate(event.getMovieId());
			break;
		case "D":
			action = syncDelete(event.getMovieId());
			break;
		default:
			throw new IllegalStateException(
					"Unknown action " + event.getAction() + " for movie "
							+ event.getMovieId()
			);
		}
		return Optional.of(
				MovieSyncResult.builder()
						.movieId(event.getMovieId())
						.action(action)
						.build()
		);
	}

	private String syncInsert(long id) {
		Optional<Movie> movie = movieRepository.findById(id);
		if (movie.isEmpty()) {
			log.debug("Should insert movie {} but was deleted", id);
			return "ID";
		} else {
			log.debug("Should insert movie {}", id);
			return "I";
		}
	}

	private String syncUpdate(long id) {
		Optional<Movie> movie = movieRepository.findById(id);
		if (movie.isEmpty()) {
			log.debug("Should update movie {} but was deleted", id);
			return "UD";
		} else {
			log.debug("Should update movie {}", id);
			return "U";
		}
	}

	private String syncDelete(long id) {
		log.debug("Should delete movie {}", id);
		return "D";
	}

}
