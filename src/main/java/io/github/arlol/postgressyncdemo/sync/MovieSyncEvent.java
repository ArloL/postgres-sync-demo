package io.github.arlol.postgressyncdemo.sync;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import io.github.arlol.postgressyncdemo.movie.Movie;
import lombok.Builder;

@Builder(toBuilder = true)
public record MovieSyncEvent(
		@Id Long id,
		String action,
		long movieId,
		@Transient @Value("null") Movie movie
) {

}
