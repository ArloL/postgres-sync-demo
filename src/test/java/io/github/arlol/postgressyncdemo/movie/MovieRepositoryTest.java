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
		assertThat(batman.id()).isNotNull();

		Movie begins = movieRepository
				.save(batman.toBuilder().title("MRT Batman Begins").build());
		assertThat(begins.id()).isEqualTo(batman.id());
		assertThat(movieRepository.findAll()).extracting(Movie::title)
				.containsExactly("MRT Batman Begins");

		movieRepository.delete(begins);
		assertThat(movieRepository.findById(begins.id())).isEmpty();

		Movie terminator = movieRepository
				.save(Movie.builder().title("MRT Terminator").build());
		terminator = movieRepository
				.save(terminator.toBuilder().title("MRT Terminator 2").build());
		assertThat(movieRepository.findByTitle("MRT Terminator")).isEmpty();
		assertThat(movieRepository.findByTitle("MRT Terminator 2"))
				.map(Movie::id)
				.hasValue(terminator.id());

		movieRepository.delete(terminator);
		assertThat(movieRepository.findAll()).isEmpty();

		movieSyncEventRepository.deleteAll();
	}

}
