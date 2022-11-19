package io.github.arlol.postgressyncdemo.watchlist;

import org.springframework.data.annotation.Id;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class WatchList {

	@Id
	private Long id;
	private Long movieId;
	private String title;

}
