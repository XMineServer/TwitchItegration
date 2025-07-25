package ru.sidey383.twitch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableFeignClients
@ConfigurationPropertiesScan
@EnableAsync
@EnableCaching
public class TwitchItegrationApplication {

	public static void main(String[] args) {
		SpringApplication.run(TwitchItegrationApplication.class, args);
	}

}
