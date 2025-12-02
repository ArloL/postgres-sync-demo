package io.github.arlol.postgressyncdemo.sync;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.ObjectMapper;

public class MovieSyncEventSerializationTest {

	@Test
	void test() throws Exception {
		var objectMapper = new ObjectMapper();
		var input = MovieSyncEvent.builder().build();
		var serialized = objectMapper.writeValueAsString(input);
		var output = objectMapper.readValue(serialized, MovieSyncEvent.class);
		assertThat(output).isNotNull();
	}

}
