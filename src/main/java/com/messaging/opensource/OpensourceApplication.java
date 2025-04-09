package com.messaging.opensource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

@SpringBootApplication
@EnableMongoRepositories
@EnableWebSocketMessageBroker
public class OpensourceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpensourceApplication.class, args);
	}

}
