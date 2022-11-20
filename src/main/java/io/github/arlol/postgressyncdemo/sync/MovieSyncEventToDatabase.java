package io.github.arlol.postgressyncdemo.sync;

import java.util.function.Consumer;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.arlol.postgressyncdemo.movie.Movie;
import io.github.arlol.postgressyncdemo.watchlist.WatchList;
import io.github.arlol.postgressyncdemo.watchlist.WatchListRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MovieSyncEventToDatabase implements Consumer<MovieSyncEvent> {

	private final WatchListRepository watchListRepository;

	public MovieSyncEventToDatabase(WatchListRepository watchListRepository) {
		this.watchListRepository = watchListRepository;
	}

	@Override
	@Transactional
	public void accept(MovieSyncEvent movieSyncEvent) {
		log.debug("{}", movieSyncEvent);
		Movie movie = movieSyncEvent.getMovie();
		switch (movieSyncEvent.getAction()) {
		case "I":
			watchListRepository.save(
					WatchList.builder()
							.movieId(movie.getId())
							.title(movie.getTitle())
							.build()
			);
			break;
		case "U":
			watchListRepository.findByMovieId(movie.getId())
					.map(wl -> wl.toBuilder().title(movie.getTitle()).build())
					.map(watchListRepository::save);
			break;
		case "D":
			watchListRepository.deleteById(movieSyncEvent.getMovieId());
			break;
		case "UD":
			watchListRepository.deleteById(movieSyncEvent.getMovieId());
			break;
		case "ID":
			watchListRepository.deleteById(movieSyncEvent.getMovieId());
			break;
		default:
			throw new IllegalStateException(
					"Unknown action " + movieSyncEvent.getAction()
							+ " for movie " + movieSyncEvent.getMovie().getId()
			);
		}
	}

}
