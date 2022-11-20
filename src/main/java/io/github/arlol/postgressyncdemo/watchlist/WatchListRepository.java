package io.github.arlol.postgressyncdemo.watchlist;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WatchListRepository extends CrudRepository<WatchList, Long> {

	void deleteByMovieId(Long movieId);

	Optional<WatchList> findByMovieId(long id);

}
