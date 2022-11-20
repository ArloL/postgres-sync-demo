package io.github.arlol.postgressyncdemo.tools;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;

import org.junit.jupiter.api.Test;

class DurationMathTest {

	@Test
	void minEquals() {
		Duration a = Duration.ofSeconds(1);
		Duration b = Duration.ofSeconds(1);
		Duration expected = a;
		Duration actual = DurationMath.min(a, b);
		assertEquals(expected, actual);
	}

	@Test
	void minASmaller() {
		Duration a = Duration.ofSeconds(1);
		Duration b = Duration.ofSeconds(10);
		Duration expected = a;
		Duration actual = DurationMath.min(a, b);
		assertEquals(expected, actual);
	}

	@Test
	void minBSmaller() {
		Duration a = Duration.ofSeconds(10);
		Duration b = Duration.ofSeconds(1);
		Duration expected = b;
		Duration actual = DurationMath.min(a, b);
		assertEquals(expected, actual);
	}

	@Test
	void maxEquals() {
		Duration a = Duration.ofSeconds(1);
		Duration b = Duration.ofSeconds(1);
		Duration expected = a;
		Duration actual = DurationMath.max(a, b);
		assertEquals(expected, actual);
	}

	@Test
	void maxASmaller() {
		Duration a = Duration.ofSeconds(1);
		Duration b = Duration.ofSeconds(10);
		Duration expected = b;
		Duration actual = DurationMath.max(a, b);
		assertEquals(expected, actual);
	}

	@Test
	void maxBSmaller() {
		Duration a = Duration.ofSeconds(10);
		Duration b = Duration.ofSeconds(1);
		Duration expected = a;
		Duration actual = DurationMath.max(a, b);
		assertEquals(expected, actual);
	}

	@Test
	void betweenWorks() {
		Duration min = Duration.ofSeconds(1);
		Duration max = Duration.ofSeconds(10);
		Duration duration = Duration.ofSeconds(5);
		Duration expected = duration;
		Duration actual = DurationMath.between(duration, min, max);
		assertEquals(expected, actual);
	}

	@Test
	void betweenMin() {
		Duration min = Duration.ofSeconds(1);
		Duration max = Duration.ofSeconds(10);
		Duration duration = Duration.ofSeconds(0);
		Duration expected = min;
		Duration actual = DurationMath.between(duration, min, max);
		assertEquals(expected, actual);
	}

	@Test
	void betweenMax() {
		Duration min = Duration.ofSeconds(1);
		Duration max = Duration.ofSeconds(10);
		Duration duration = Duration.ofSeconds(20);
		Duration expected = max;
		Duration actual = DurationMath.between(duration, min, max);
		assertEquals(expected, actual);
	}

	@Test
	void betweenIllegal() {
		Duration min = Duration.ofSeconds(10);
		Duration max = Duration.ofSeconds(1);
		Duration duration = Duration.ofSeconds(5);
		assertThrows(IllegalStateException.class, () -> {
			DurationMath.between(duration, min, max);
		});
	}

}
