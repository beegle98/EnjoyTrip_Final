package com.ssafy.tournest;

import com.ssafy.tournest.config.properties.AppProperties;
import com.ssafy.tournest.config.properties.CorsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
		CorsProperties.class,
		AppProperties.class
})
public class TournestApplication {
	public static Long userId = 1L;
	public static void main(String[] args) {
		SpringApplication.run(TournestApplication.class, args);
	}

}
