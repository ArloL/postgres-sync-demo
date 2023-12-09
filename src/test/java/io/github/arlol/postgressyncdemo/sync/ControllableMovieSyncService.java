package io.github.arlol.postgressyncdemo.sync;

import java.util.function.Consumer;

import io.github.arlol.postgressyncdemo.movie.MovieRepository;

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

	private boolean enabled;

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

}
