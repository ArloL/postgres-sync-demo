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
	@Transactional("transactionManager")
	public void accept(MovieSyncEvent movieSyncEvent) {
		log.debug("{}", movieSyncEvent);
		Movie movie = movieSyncEvent.movie();
		switch (movieSyncEvent.action()) {
		case "I":
			watchListRepository.save(
					WatchList.builder()
							.movieId(movie.id())
							.title(movie.title())
							.build()
			);
			break;
		case "U":
			watchListRepository.findByMovieId(movie.id())
					.map(wl -> wl.toBuilder().title(movie.title()).build())
					.map(watchListRepository::save);
			break;
		case "D":
			watchListRepository.deleteById(movieSyncEvent.movieId());
			break;
		default:
			throw new IllegalStateException(
					"Unknown action " + movieSyncEvent.action() + " for movie "
							+ movieSyncEvent.movie().id()
			);
		}
	}

}
