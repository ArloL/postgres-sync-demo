package io.github.arlol.postgressyncdemo.movie;

import org.springframework.data.annotation.Id;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class Movie {

	@Id
	private Long id;
	private String title;

}
