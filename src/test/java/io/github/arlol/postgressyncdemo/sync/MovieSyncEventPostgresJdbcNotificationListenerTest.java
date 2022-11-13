package io.github.arlol.postgressyncdemo.sync;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.github.arlol.postgressyncdemo.movie.Movie;
import io.github.arlol.postgressyncdemo.movie.MovieRepository;

@SpringBootTest
@ActiveProfiles("postgres")
public class MovieSyncEventPostgresJdbcNotificationListenerTest {

	@Autowired
	MovieSyncEventPostgresJdbcNotificationListener service;
	@Autowired
	MovieRepository repository;

	@Test
	void testName() throws Exception {
		Movie batman = repository
				.save(Movie.builder().title("MSEPNLT Batman").build());
		Thread.sleep(1000L);

		batman = repository.save(
				batman.toBuilder().title("MSEPNLT Batman Begins").build()
		);
		Thread.sleep(1000L);
		repository.delete(batman);

		Movie terminator = repository
				.save(Movie.builder().title("MSEPNLT Terminator").build());
		Thread.sleep(1000L);

		terminator = repository.save(
				terminator.toBuilder().title("MSEPNLT Terminator 2").build()
		);
		Thread.sleep(1000L);

		repository.delete(terminator);
		Thread.sleep(1000L);
	}

}
