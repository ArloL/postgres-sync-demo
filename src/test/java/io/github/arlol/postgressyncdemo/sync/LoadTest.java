package io.github.arlol.postgressyncdemo.sync;

import static java.math.RoundingMode.HALF_UP;
import static org.awaitility.Awaitility.await;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.stream.IntStream;

import org.slf4j.Logger;

import io.github.arlol.postgressyncdemo.movie.Movie;
import io.github.arlol.postgressyncdemo.movie.MovieRepository;
import io.github.arlol.postgressyncdemo.watchlist.WatchListRepository;

public class LoadTest {

	public static final int COUNT = 10_000;

	private static BigDecimal calculatePerSeconds(long start, long end) {
		BigDecimal duration = BigDecimal
				.valueOf(Duration.ofNanos(end - start).toNanos());
		BigDecimal oneSecond = BigDecimal
				.valueOf(Duration.ofSeconds(1).toNanos());
		BigDecimal timePer = duration
				.divide(BigDecimal.valueOf(COUNT), HALF_UP);
		return oneSecond.divide(timePer, HALF_UP);
	}

	private Logger log;
	private MovieSyncEventDatabaseListener listener;
	private MovieSyncService movieSyncService;
	private MovieRepository movieRepository;
	private MovieSyncEventRepository movieSyncEventRepository;
	private WatchListRepository watchListRepository;

	public LoadTest(
			Logger log,
			MovieSyncEventDatabaseListener listener,
			MovieSyncService movieSyncService,
			MovieRepository movieRepository,
			MovieSyncEventRepository movieSyncEventRepository,
			WatchListRepository watchListRepository
	) {
		super();
		this.log = log;
		this.listener = listener;
		this.movieSyncService = movieSyncService;
		this.movieRepository = movieRepository;
		this.movieSyncEventRepository = movieSyncEventRepository;
		this.watchListRepository = watchListRepository;
	}

	public void afterEach() throws Exception {
		movieSyncService.setEnabled(false);
		listener.close();
	}

	public void beforeEach() throws Exception {
		movieSyncService.setEnabled(true);
		listener.start();
		await().until(listener::isListening);
	}

	public void test() {
		log.info("start");

		long start = System.nanoTime();

		createAndUpdate();

		await().atMost(Duration.ofSeconds(360))
				.until(() -> movieSyncEventRepository.count() == 0);
		await().atMost(Duration.ofSeconds(360))
				.until(() -> watchListRepository.count() == COUNT);

		delete();

		await().atMost(Duration.ofSeconds(360))
				.until(() -> movieSyncEventRepository.count() == 0);
		await().atMost(Duration.ofSeconds(360))
				.until(() -> watchListRepository.count() == 0);

		log.info(
				"ops per second: {}",
				LoadTest.calculatePerSeconds(start, System.nanoTime())
		);
	}

	private void createAndUpdate() {
		IntStream.range(0, COUNT).parallel().forEach(i -> {
			movieRepository.save(Movie.builder().title("c" + i).build());
		});
		IntStream.range(0, COUNT).parallel().forEach(i -> {
			movieRepository.findByTitle("c" + i)
					.ifPresent(
							movie -> movieRepository.save(
									movie.toBuilder().title("d" + i).build()
							)
					);
		});
	}

	private void delete() {
		IntStream.range(0, COUNT).parallel().forEach(i -> {
			movieRepository.findByTitle("a" + i)
					.ifPresent(movie -> movieRepository.delete(movie));
			movieRepository.findByTitle("d" + i)
					.ifPresent(movie -> movieRepository.delete(movie));
		});
	}

}
