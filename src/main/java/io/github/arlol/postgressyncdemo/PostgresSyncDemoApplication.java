package io.github.arlol.postgressyncdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PostgresSyncDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(PostgresSyncDemoApplication.class, args);
	}

}
