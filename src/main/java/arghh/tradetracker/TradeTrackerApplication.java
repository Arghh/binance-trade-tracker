package arghh.tradetracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@SpringBootApplication
public class TradeTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TradeTrackerApplication.class, args);

	}
}
