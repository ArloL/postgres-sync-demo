package io.github.arlol.postgressyncdemo.sync;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import io.github.arlol.postgressyncdemo.DatabaseTest;
import io.github.arlol.postgressyncdemo.movie.Movie;
import io.github.arlol.postgressyncdemo.movie.MovieRepository;

@SpringBootTest
public class MovieSyncServiceTest extends DatabaseTest {

	@Autowired
	MovieRepository movieRepository;
	@Autowired
	MovieSyncEventRepository movieSyncEventRepository;

	@Test
	@Commit
	void testName() throws Exception {
		MovieSyncService movieSyncService = new MovieSyncService(
				movieSyncEventRepository,
				movieRepository,
				true
		);

		Movie batman = movieRepository
				.save(Movie.builder().title("MSEPNLT Batman").build());

		assertThat(movieSyncService.sync()).isPresent()
				.map(o -> o.getAction())
				.hasValue("I");
		;

		batman = movieRepository.save(
				batman.toBuilder().title("MSEPNLT Batman Begins").build()
		);

		assertThat(movieSyncService.sync()).isPresent()
				.map(o -> o.getAction())
				.hasValue("U");
		;

		movieRepository.delete(batman);

		assertThat(movieSyncService.sync()).isPresent()
				.map(o -> o.getAction())
				.hasValue("D");
		;

		Movie terminator = movieRepository
				.save(Movie.builder().title("MSEPNLT Terminator").build());

		assertThat(movieSyncService.sync()).isPresent()
				.map(o -> o.getAction())
				.hasValue("I");
		;

		terminator = movieRepository.save(
				terminator.toBuilder().title("MSEPNLT Terminator 2").build()
		);

		movieRepository.delete(terminator);

		assertThat(movieSyncService.sync()).isPresent()
				.map(o -> o.getAction())
				.hasValue("UD");
		assertThat(movieSyncService.sync()).isPresent()
				.map(o -> o.getAction())
				.hasValue("D");
		;
	}

}