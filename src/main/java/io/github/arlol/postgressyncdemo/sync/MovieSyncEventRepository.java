package io.github.arlol.postgressyncdemo.sync;

import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieSyncEventRepository
		extends CrudRepository<MovieSyncEvent, Long> {

	@Query("""
			DELETE FROM
			    movie_sync_event
			USING (
			    SELECT * FROM movie_sync_event LIMIT 1 FOR UPDATE SKIP LOCKED
			) q
			WHERE q.id = movie_sync_event.id RETURNING movie_sync_event.*
			""")
	Optional<MovieSyncEvent> findAndDeleteNextSyncEvent();

	@Override
	@SuppressWarnings("unchecked")
	default MovieSyncEvent save(MovieSyncEvent entity) {
		throw new UnsupportedOperationException("Writes are not allowed");
	}

}
