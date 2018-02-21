package arghh.profittracker;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import arghh.tradetracker.TradeTrackerApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { TradeTrackerApplication.class })
@ActiveProfiles("test")
public class TradeTrackerApplicationTest {

	@Test
	public void contextLoads() {
	}

}