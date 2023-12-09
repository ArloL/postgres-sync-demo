package io.github.arlol.postgressyncdemo.sync;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import io.github.arlol.postgressyncdemo.DatabaseTest;
import io.github.arlol.postgressyncdemo.movie.Movie;

@ActiveProfiles("postgres-with-trigger")
public class MovieSyncEventRepositoryTest extends DatabaseTest {

	@Test
	void testFindAll() throws Exception {
		assertThat(movieSyncEventRepository.findAll()).isEmpty();
	}

	@Test
	void testSave() throws Exception {
		Assertions.assertThrows(UnsupportedOperationException.class, () -> {
			movieSyncEventRepository
					.save(MovieSyncEvent.builder().action("I").build());
		});
	}

	@Test
	void testFindAndDeleteNextSyncEvent() throws Exception {
		assertThat(movieSyncEventRepository.findAll()).isEmpty();

		var nextSyncEvent = movieSyncEventRepository
				.findAndDeleteNextSyncEvent();
		assertThat(nextSyncEvent).isEmpty();

		Movie movie = Movie.builder().title("MSERT Batman").build();
		movie = movieRepository.save(movie);
		movie = movieRepository
				.save(movie.toBuilder().title("MSERT Batman Begins").build());
		movieRepository.delete(movie);

		assertThat(movieSyncEventRepository.findAll()).isNotEmpty();

		nextSyncEvent = movieSyncEventRepository.findAndDeleteNextSyncEvent();
		assertThat(nextSyncEvent).isNotEmpty();
		assertThat(nextSyncEvent.orElseThrow().getAction()).isEqualTo("I");
		assertThat(nextSyncEvent.orElseThrow().getMovieId())
				.isEqualTo(movie.getId());

		nextSyncEvent = movieSyncEventRepository.findAndDeleteNextSyncEvent();
		assertThat(nextSyncEvent).isNotEmpty();
		assertThat(nextSyncEvent.orElseThrow().getAction()).isEqualTo("U");
		assertThat(nextSyncEvent.orElseThrow().getMovieId())
				.isEqualTo(movie.getId());

		nextSyncEvent = movieSyncEventRepository.findAndDeleteNextSyncEvent();
		assertThat(nextSyncEvent).isNotEmpty();
		assertThat(nextSyncEvent.orElseThrow().getAction()).isEqualTo("D");
		assertThat(nextSyncEvent.orElseThrow().getMovieId())
				.isEqualTo(movie.getId());

		assertThat(movieSyncEventRepository.findAll()).isEmpty();
	}

}
