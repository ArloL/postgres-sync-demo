package io.github.arlol.postgressyncdemo.sync;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MovieSyncEventRabbitListener {

	private final MovieSyncEventToDatabase processor;

	public MovieSyncEventRabbitListener(MovieSyncEventToDatabase processor) {
		this.processor = processor;
	}

	@RabbitListener(queues = "#{syncEventQueue}")
	public void receiveMessageFromFanout(MovieSyncEvent event) {
		processor.accept(event);
	}

}
