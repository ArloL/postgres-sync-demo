package io.github.arlol.postgressyncdemo.movie;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.ObjectMapper;

public class MovieSerializationTest {

	@Test
	void test() throws Exception {
		var objectMapper = new ObjectMapper();
		var input = Movie.builder().build();
		var serialized = objectMapper.writeValueAsString(input);
		var output = objectMapper.readValue(serialized, Movie.class);
		assertThat(output).isNotNull();
	}

}
