package io.github.arlol.postgressyncdemo.sync;

import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieSyncEventRepository
		extends CrudRepository<MovieSyncEvent, Long> {

	static final String FIND_AND_DELETE_NEXT_SYNC_EVENT_QUERY = """
			WITH target_rows AS MATERIALIZED (
				SELECT id
				FROM movie_sync_event
				ORDER BY id
				LIMIT 1
				FOR UPDATE
				SKIP LOCKED
			)
			DELETE FROM movie_sync_event
			WHERE id IN (SELECT * FROM target_rows)
			RETURNING *
			""";

	@Query(FIND_AND_DELETE_NEXT_SYNC_EVENT_QUERY)
	Optional<MovieSyncEvent> findAndDeleteNextSyncEvent();

	@Override
	@SuppressWarnings("unchecked")
	default MovieSyncEvent save(MovieSyncEvent entity) {
		throw new UnsupportedOperationException("Writes are not allowed");
	}

}
