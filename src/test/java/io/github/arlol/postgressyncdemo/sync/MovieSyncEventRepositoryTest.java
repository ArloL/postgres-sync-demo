package io.github.arlol.postgressyncdemo.sync;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.github.arlol.postgressyncdemo.DatabaseTest;
import io.github.arlol.postgressyncdemo.movie.Movie;

@SpringBootTest
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
		assertThat(nextSyncEvent.get().getAction()).isEqualTo("I");
		assertThat(nextSyncEvent.get().getMovieId()).isEqualTo(movie.getId());

		nextSyncEvent = movieSyncEventRepository.findAndDeleteNextSyncEvent();
		assertThat(nextSyncEvent).isNotEmpty();
		assertThat(nextSyncEvent.get().getAction()).isEqualTo("U");
		assertThat(nextSyncEvent.get().getMovieId()).isEqualTo(movie.getId());

		nextSyncEvent = movieSyncEventRepository.findAndDeleteNextSyncEvent();
		assertThat(nextSyncEvent).isNotEmpty();
		assertThat(nextSyncEvent.get().getAction()).isEqualTo("D");
		assertThat(nextSyncEvent.get().getMovieId()).isEqualTo(movie.getId());

		assertThat(movieSyncEventRepository.findAll()).isEmpty();
	}

}
