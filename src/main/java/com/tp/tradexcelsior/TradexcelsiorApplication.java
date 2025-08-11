package com.tp.tradexcelsior;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.CrossOrigin;

@OpenAPIDefinition(
		info = @Info(
				title = "TradeExcelsior API",
				version = "1.0",
				description = "API documentation for the TradeExcelsior Admin Panel"
		)
)
@SpringBootApplication
@EnableScheduling
@CrossOrigin("*")
public class TradexcelsiorApplication {

	public static void main(String[] args) {
		SpringApplication.run(TradexcelsiorApplication.class, args);
	}
}