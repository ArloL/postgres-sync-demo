package io.github.arlol.postgressyncdemo.sync;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import io.github.arlol.postgressyncdemo.DatabaseTest;
import io.github.arlol.postgressyncdemo.watchlist.WatchList;

public class MovieSyncEventToDatabaseTest extends DatabaseTest {

	@Autowired
	MovieSyncEventToDatabase processor;

	@Test
	void deleteRemovesTheWatchListEntryOfTheMovie() throws Exception {
		// The watch list has its own identity column, so its ids only line up
		// with the movie ids by coincidence. Pick a movie id that can't match
		WatchList entry = watchListRepository.save(
				WatchList.builder()
						.movieId(4242L)
						.title("MSETDT Batman")
						.build()
		);
		assertThat(entry.id()).isNotEqualTo(4242L);

		processor.accept(
				MovieSyncEvent.builder().action("D").movieId(4242L).build()
		);

		assertThat(watchListRepository.findAll()).isEmpty();
	}

}
