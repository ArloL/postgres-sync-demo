package io.github.arlol.postgressyncdemo.movie;

import org.springframework.data.annotation.Id;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class Movie {

	@Id
	Long id;
	String title;

}
