package com.AITrial.AIEventsTry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AiTrialApplication {

	public static void main(String[] args) {
		SpringApplication.run(AiTrialApplication.class, args);
	}

}
