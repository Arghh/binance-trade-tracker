package arghh.profittracker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import arghh.tradetracker.TradeTrackerApplication;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { TradeTrackerApplication.class })
@ActiveProfiles("test")
public class TradeTrackerApplicationTest {

	@Test
	public void contextLoads() {
	}

}