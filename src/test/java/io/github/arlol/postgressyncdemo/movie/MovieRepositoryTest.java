package io.github.arlol.postgressyncdemo.movie;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("postgres")
public class MovieRepositoryTest {

	@Autowired
	MovieRepository repository;

	@Test
	void testFindAll() throws Exception {
		assertThat(repository.findAll()).isEmpty();
	}

	@Test
	void testSave() throws Exception {
		Movie batman = repository.save(Movie.builder().title("Batman").build());
		batman = repository
				.save(batman.toBuilder().title("Batman Begins").build());

		Movie terminator = repository
				.save(Movie.builder().title("Terminator").build());
		terminator = repository
				.save(terminator.toBuilder().title("Terminator 2").build());
		repository.delete(terminator);
	}

}
