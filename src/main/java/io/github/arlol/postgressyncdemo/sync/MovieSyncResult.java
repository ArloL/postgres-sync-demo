package io.github.arlol.postgressyncdemo.sync;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class MovieSyncResult {

	private String action;
	private long movieId;

}
