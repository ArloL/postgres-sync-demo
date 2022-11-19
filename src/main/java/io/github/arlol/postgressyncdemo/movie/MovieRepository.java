package io.github.arlol.postgressyncdemo.movie;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository
		extends PagingAndSortingRepository<Movie, Long> {

	Optional<Movie> findByTitle(String title);

}
