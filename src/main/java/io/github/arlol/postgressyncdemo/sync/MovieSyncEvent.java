package io.github.arlol.postgressyncdemo.sync;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import io.github.arlol.postgressyncdemo.movie.Movie;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder(toBuilder = true)
@Jacksonized
public class MovieSyncEvent {

	@Id
	private Long id;
	private String action;
	private long movieId;
	@Transient
	@Value("null")
	private Movie movie;

}
