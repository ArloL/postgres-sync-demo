package io.github.arlol.postgressyncdemo.watchlist;

import java.util.Optional;

import org.springframework.data.annotation.Immutable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@Immutable
public interface WatchListRepository extends CrudRepository<WatchList, Long> {

	Optional<WatchList> findByMovieId(long id);

}
