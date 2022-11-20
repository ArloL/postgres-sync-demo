package io.github.arlol.postgressyncdemo.sync;

import java.util.function.Consumer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MovieSyncEventToRabbit implements Consumer<MovieSyncEvent> {

	private final RabbitTemplate rabbitTemplate;
	private final String exchangeName;

	public MovieSyncEventToRabbit(
			RabbitTemplate rabbitTemplate,
			@Value("#{syncEventExchange.getName()}") String exchangeName
	) {
		this.rabbitTemplate = rabbitTemplate;
		this.exchangeName = exchangeName;
	}

	@Override
	public void accept(MovieSyncEvent movieSyncEvent) {
		log.debug("{}", movieSyncEvent);
		rabbitTemplate.convertAndSend(exchangeName, "", movieSyncEvent);
	}

}
