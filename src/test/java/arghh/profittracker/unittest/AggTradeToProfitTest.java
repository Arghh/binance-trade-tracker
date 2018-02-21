package arghh.profittracker.unittest;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import arghh.tradetracker.converters.AggTradeToProfit;
import arghh.tradetracker.model.AggregatedTrade;
import arghh.tradetracker.model.Profit;
import arghh.tradetracker.services.BaseCurrency;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { AggTradeToProfit.class })
@ActiveProfiles("test")
public class AggTradeToProfitTest {

	@Autowired
	private AggTradeToProfit converter;

	@Test
	public void aggTradeToProfitConverterTest() throws Exception {

		List<AggregatedTrade> trades = new ArrayList<>();

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateBuyString = "22-01-2018 10:20:56";
		String dateSellString = "23-01-2018 11:21:56";
		Date buyDate = formatter.parse(dateBuyString);
		Date sellDate = formatter.parse(dateSellString);

		AggregatedTrade firstBuy = new AggregatedTrade();
		firstBuy.setId(1l);
		firstBuy.setBuy(true);
		firstBuy.setQuantity(new BigDecimal("10"));
		firstBuy.setSymbol("VENETH");
		firstBuy.setPrice(new BigDecimal("0.00335000"));
		firstBuy.setTotal(new BigDecimal("0.03234100"));
		firstBuy.setTradeTime(buyDate);
		trades.add(firstBuy);

		AggregatedTrade firstSell = new AggregatedTrade();
		firstSell.setId(2l);
		firstSell.setBuy(false);
		firstSell.setQuantity(new BigDecimal("10"));
		firstSell.setSymbol("VENETH");
		firstSell.setPrice(new BigDecimal("0.00500000"));
		firstSell.setTotal(new BigDecimal("0.04280000"));
		firstSell.setTradeTime(sellDate);
		trades.add(firstSell);

		Profit profit = converter.convert(trades);

		Assert.assertTrue(profit != null);
		Assert.assertEquals(new BigDecimal("10"), profit.getQuantity());
		Assert.assertEquals(BaseCurrency.ETH, profit.getBaseCurrency());
		Assert.assertEquals(new BigDecimal("0.00165000"), profit.getPriceDifference());
		Assert.assertEquals(new BigDecimal("0.01045900"), profit.getProfitValue());
		Assert.assertEquals(firstSell.getTradeTime().getTime() - firstBuy.getTradeTime().getTime(),
				profit.getTimeDifference());
	}
}
