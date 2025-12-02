package io.github.arlol.postgressyncdemo.movie;

import org.springframework.data.annotation.Id;

import lombok.Builder;

@Builder(toBuilder = true)
public record Movie(
		@Id Long id,
		String title
) {

}
