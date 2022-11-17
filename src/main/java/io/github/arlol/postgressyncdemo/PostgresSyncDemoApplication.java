package io.github.arlol.postgressyncdemo;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
// Explicit import due to r2dbc and jdbc being on the classpath
@Import(DataSourceAutoConfiguration.class)
public class PostgresSyncDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(PostgresSyncDemoApplication.class, args);
	}

	@Bean
	public Executor singleThreadExecutor() {
		return new ThreadPoolExecutor(
				1,
				1,
				0L,
				TimeUnit.MILLISECONDS,
				new LinkedBlockingDeque<>(1)
		);
	}

}
