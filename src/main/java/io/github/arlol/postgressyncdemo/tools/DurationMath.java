package io.github.arlol.postgressyncdemo.tools;

import java.time.Duration;

public final class DurationMath {

	private DurationMath() {
	}

	public static Duration min(Duration a, Duration b) {
		if (a.compareTo(b) < 0) {
			return a;
		}
		return b;
	}

	public static Duration max(Duration a, Duration b) {
		if (a.compareTo(b) < 0) {
			return b;
		}
		return a;
	}

	public static Duration between(
			Duration duration,
			Duration min,
			Duration max
	) {
		if (max.compareTo(min) < 0) {
			throw new IllegalStateException();
		}
		return min(max, max(min, duration));
	}

}
