package io.github.arlol.postgressyncdemo.watchlist;

import java.util.Optional;

import org.springframework.data.annotation.Immutable;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@Immutable
public interface WatchListRepository extends CrudRepository<WatchList, Long> {

	Optional<WatchList> findByMovieId(long id);

	@Modifying
	@Query("DELETE FROM watch_list WHERE movie_id = :movieId")
	void deleteByMovieId(long movieId);

}
