package io.github.arlol.postgressyncdemo.sync;

import java.util.function.Consumer;

import io.github.arlol.postgressyncdemo.movie.MovieRepository;
import lombok.Getter;
import lombok.Setter;

public class ControllableMovieSyncService extends MovieSyncService {

	public ControllableMovieSyncService(
			MovieSyncEventRepository movieSyncEventRepository,
			MovieRepository movieRepository,
			Consumer<MovieSyncEvent> movieSyncEventProcessor,
			boolean enabled
	) {
		super(
				movieSyncEventRepository,
				movieRepository,
				movieSyncEventProcessor,
				enabled
		);
	}

	@Setter
	@Getter
	private boolean enabled;

}
