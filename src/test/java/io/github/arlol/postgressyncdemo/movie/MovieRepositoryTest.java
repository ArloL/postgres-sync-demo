package io.github.arlol.postgressyncdemo.movie;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import io.github.arlol.postgressyncdemo.DatabaseTest;

@SpringBootTest
public class MovieRepositoryTest extends DatabaseTest {

	@Test
	void testFindAll() throws Exception {
		assertThat(movieRepository.findAll()).isEmpty();
	}

	@Test
	void testSave() throws Exception {
		Movie batman = movieRepository
				.save(Movie.builder().title("MRT Batman").build());
		batman = movieRepository
				.save(batman.toBuilder().title("MRT Batman Begins").build());
		movieRepository.delete(batman);

		Movie terminator = movieRepository
				.save(Movie.builder().title("MRT Terminator").build());
		terminator = movieRepository
				.save(terminator.toBuilder().title("MRT Terminator 2").build());
		movieRepository.delete(terminator);

		movieSyncEventRepository.deleteAll();
	}

}
