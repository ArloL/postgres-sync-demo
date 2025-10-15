package io.github.arlol.postgressyncdemo.watchlist;

import org.springframework.data.annotation.Id;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class WatchList {

	@Id
	Long id;
	Long movieId;
	String title;

}
