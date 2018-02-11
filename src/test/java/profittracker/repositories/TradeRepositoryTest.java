package profittracker.repositories;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import tradetracker.model.Trade;
import tradetracker.repositories.TradeRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TradeRepository.class)
public class TradeRepositoryTest {

	private static final BigDecimal PRICE = BigDecimal.valueOf(100.00);
	private static final String SYMBOL = "WTCBTC";

	@Autowired
	private TradeRepository tradeRepo;

	@Before
	public void setUp() throws Exception {

	}

	@Test
	public void testPersistence() {
		// given
		Trade trade = new Trade();
		trade.setPrice(PRICE);
		trade.setSymbol(SYMBOL);
		// when
		tradeRepo.save(trade);

		// then
		Assert.assertNotNull(trade.getId());
		Trade newProduct = tradeRepo.findById(trade.getId()).orElse(null);
		Assert.assertEquals((Long) 1L, newProduct.getId());
	}
}