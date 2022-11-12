package io.github.arlol.postgressyncdemo.sync;

import org.springframework.data.annotation.Id;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class MovieSyncEvent {

	@Id
	private Long id;
	private String action;
	private long movieId;

}
