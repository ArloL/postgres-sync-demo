package io.github.arlol.postgressyncdemo.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("rabbitmq")
@Import(RabbitAutoConfiguration.class)
public class MessagingConfig {

	@Bean
	public MessageConverter jsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	public Queue syncEventQueue() {
		// see https://www.rabbitmq.com/consumers.html#single-active-consumer
		return QueueBuilder.durable()
				.withArgument("x-single-active-consumer", true)
				.build();
	}

	@Bean
	public FanoutExchange syncEventExchange() {
		return ExchangeBuilder.fanoutExchange("movies.exchange").build();
	}

	@Bean
	public Binding syncEventBinding(
			Queue syncEventQueue,
			FanoutExchange syncEventExchange
	) {
		return BindingBuilder.bind(syncEventQueue).to(syncEventExchange);
	}

}
