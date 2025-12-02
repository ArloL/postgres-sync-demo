package io.github.arlol.postgressyncdemo.watchlist;

import org.springframework.data.annotation.Id;

import lombok.Builder;

@Builder(toBuilder = true)
public record WatchList(
		@Id Long id,
		Long movieId,
		String title
) {

}
