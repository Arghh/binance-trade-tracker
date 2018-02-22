package arghh.profittracker.integrationtests;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import arghh.tradetracker.TradeTrackerApplication;
import arghh.tradetracker.model.AggregatedTrade;
import arghh.tradetracker.model.Profit;
import arghh.tradetracker.repositories.AggregatedTradeRepository;
import arghh.tradetracker.repositories.ProfitRepository;
import arghh.tradetracker.services.AggregatedTradeService;
import arghh.tradetracker.services.BaseCurrency;
import arghh.tradetracker.services.ProfitService;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest(classes = { TradeTrackerApplication.class })

public class ProfitServiceTest {

	@Autowired
	private ProfitService profitService;

	@Autowired
	private ProfitRepository profitRepository;

	@MockBean
	private AggregatedTradeService aggTradeService;

	@MockBean
	private AggregatedTradeRepository aggTradeRepository;

	// @PersistenceContext
	// private EntityManager entityManager;

	@Test
	@Transactional
	public void saveAndListAllProfits() throws Exception {
		Profit profit1 = new Profit();
		profit1.setQuantity(new BigDecimal("10"));
		profit1.setProfitValue(new BigDecimal("1"));

		Profit profit2 = new Profit();
		profit2.setQuantity(new BigDecimal("0.001"));
		profit2.setProfitValue(new BigDecimal("1"));

		profitService.saveOrUpdate(profit1);
		profitService.saveOrUpdate(profit2);

		List<Profit> profits = profitService.listAllProfits();

		Assert.assertTrue(profit1.getId() != null);
		Assert.assertTrue(profit2.getId() != null);
		System.out.println(profit1.getId());
		Assert.assertEquals(2, profits.size());
		Assert.assertEquals(new BigDecimal("10"), profit1.getQuantity());
		Assert.assertEquals(new BigDecimal("0.001"), profit2.getQuantity());
	}

	@Test
	@Transactional
	public void saveSimpleTradePairTest() throws Exception {

		List<AggregatedTrade> trades = new ArrayList<>();
		List<String> symbols = new ArrayList<>();
		symbols.add("ETHBTC");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String dateBuyString = "22-01-2018 10:20:56";
		String dateSellString = "23-01-2018 11:21:56";
		Date buyDate = formatter.parse(dateBuyString);
		Date sellDate = formatter.parse(dateSellString);
		AggregatedTrade buy = new AggregatedTrade();
		buy.setBuy(true);
		buy.setQuantity(new BigDecimal("0.5"));
		buy.setPrice(new BigDecimal("0.1"));
		buy.setTotal(new BigDecimal("0.2"));
		buy.setFee(new BigDecimal("0.1"));
		buy.setFeeCoin("BNB");
		buy.setSymbol("ETHBTC");
		buy.setTradeTime(buyDate);
		trades.add(buy);

		AggregatedTrade sell = new AggregatedTrade();
		sell.setBuy(false);
		sell.setQuantity(new BigDecimal("0.5"));
		sell.setPrice(new BigDecimal("0.2"));
		sell.setTotal(new BigDecimal("0.3"));
		sell.setFee(new BigDecimal("0.1"));
		sell.setFeeCoin("BNB");
		sell.setSymbol("ETHBTC");
		sell.setTradeTime(sellDate);
		trades.add(sell);

		Mockito.when(aggTradeService.listAllAggregated()).thenReturn(trades);
		Mockito.when(aggTradeRepository.findDistinctSymbols()).thenReturn(symbols);
		Mockito.when(aggTradeRepository.findBySymbolAndProfitIsNull(symbols.get(0))).thenReturn(trades);

		profitService.saveAllTradeProfits();

		List<Profit> profits = profitService.listAllProfits();

		Assert.assertTrue(!profits.isEmpty());
		Assert.assertEquals(1, profits.size());
		Assert.assertEquals(new BigDecimal("0.5"), profits.get(0).getQuantity());
		Assert.assertEquals(BaseCurrency.BTC, profits.get(0).getBaseCurrency());
		Assert.assertEquals(new BigDecimal("0.1"), profits.get(0).getProfitValue());
		Assert.assertEquals(sell.getTradeTime().getTime() - buy.getTradeTime().getTime(),
				profits.get(0).getTimeDifference());
	}

	@Test
	@Transactional
	public void saveTradePairWithFeeCoinNotBnBTest() throws Exception {

		List<AggregatedTrade> trades = new ArrayList<>();
		List<String> symbols = new ArrayList<>();
		symbols.add("ETHBTC");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String dateBuyString = "22-01-2018 10:20:56";
		String dateSellString = "23-01-2018 11:21:56";
		Date buyDate = formatter.parse(dateBuyString);
		Date sellDate = formatter.parse(dateSellString);
		AggregatedTrade buy = new AggregatedTrade();
		buy.setBuy(true);
		// buy sell quantity is diffrent but is ignored because fee coin is not BNB
		buy.setQuantity(new BigDecimal("0.6"));
		buy.setPrice(new BigDecimal("0.1"));
		buy.setTotal(new BigDecimal("0.2"));
		buy.setFee(new BigDecimal("0.1"));
		buy.setFeeCoin("ETH");
		buy.setSymbol("ETHBTC");
		buy.setTradeTime(buyDate);
		trades.add(buy);

		AggregatedTrade sell = new AggregatedTrade();
		sell.setBuy(false);
		sell.setQuantity(new BigDecimal("0.5"));
		sell.setPrice(new BigDecimal("0.2"));
		sell.setTotal(new BigDecimal("0.3"));
		sell.setFee(new BigDecimal("0.1"));
		sell.setFeeCoin("BTC");
		sell.setSymbol("ETHBTC");
		sell.setTradeTime(sellDate);
		trades.add(sell);

		Mockito.when(aggTradeService.listAllAggregated()).thenReturn(trades);
		Mockito.when(aggTradeRepository.findDistinctSymbols()).thenReturn(symbols);
		Mockito.when(aggTradeRepository.findBySymbolAndProfitIsNull(symbols.get(0))).thenReturn(trades);

		profitService.saveAllTradeProfits();

		List<Profit> profits = profitService.listAllProfits();

		Assert.assertTrue(!profits.isEmpty());
		Assert.assertEquals(1, profits.size());
		// TODO: atm the quantity is buy quantity could maybe set to sell because i dont
		// sell all i buy.
		// TODO: test if the buy and sell pair get paired to the correct profit
		Assert.assertEquals(new BigDecimal("0.6"), profits.get(0).getQuantity());
		Assert.assertEquals(BaseCurrency.BTC, profits.get(0).getBaseCurrency());
		Assert.assertEquals(new BigDecimal("0.1"), profits.get(0).getProfitValue());
	}

	@Test
	@Transactional
	public void saveUnclearBuyTradeWithSellListTest() throws Exception {

		List<AggregatedTrade> trades = new ArrayList<>();
		List<String> symbols = new ArrayList<>();
		symbols.add("ETHBTC");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String dateBuyString = "22-01-2018 10:20:56";
		String dateSell1String = "23-01-2018 11:21:56";
		String dateSell2String = "24-01-2018 12:31:56";
		String dateSell3String = "25-01-2018 13:51:56";

		Date buyDate = formatter.parse(dateBuyString);
		Date sellDate1 = formatter.parse(dateSell1String);
		Date sellDate2 = formatter.parse(dateSell2String);
		Date sellDate3 = formatter.parse(dateSell3String);

		AggregatedTrade buy = new AggregatedTrade();
		buy.setBuy(true);
		buy.setQuantity(new BigDecimal("55"));
		buy.setPrice(new BigDecimal("1"));
		buy.setTotal(new BigDecimal("55"));
		buy.setFee(new BigDecimal("0.1"));
		buy.setFeeCoin("BNB");
		buy.setSymbol("LSKETH");
		buy.setTradeTime(buyDate);
		trades.add(buy);

		AggregatedTrade sell1 = new AggregatedTrade();
		sell1.setBuy(false);
		sell1.setQuantity(new BigDecimal("5"));
		sell1.setPrice(new BigDecimal("2"));
		sell1.setTotal(new BigDecimal("10"));
		sell1.setFee(new BigDecimal("0.1"));
		sell1.setFeeCoin("BNB");
		sell1.setSymbol("LSKETH");
		sell1.setTradeTime(sellDate1);
		trades.add(sell1);

		AggregatedTrade sell2 = new AggregatedTrade();
		sell2.setBuy(false);
		sell2.setQuantity(new BigDecimal("10"));
		sell2.setPrice(new BigDecimal("2"));
		sell2.setTotal(new BigDecimal("20"));
		sell2.setFee(new BigDecimal("0.1"));
		sell2.setFeeCoin("BNB");
		sell2.setSymbol("LSKETH");
		sell2.setTradeTime(sellDate2);
		trades.add(sell2);

		AggregatedTrade sell3 = new AggregatedTrade();
		sell3.setBuy(false);
		sell3.setQuantity(new BigDecimal("40"));
		sell3.setPrice(new BigDecimal("1"));
		sell3.setTotal(new BigDecimal("40"));
		sell3.setFee(new BigDecimal("0.1"));
		sell3.setFeeCoin("BNB");
		sell3.setSymbol("LSKETH");
		sell3.setTradeTime(sellDate3);
		trades.add(sell3);

		Mockito.when(aggTradeService.listAllAggregated()).thenReturn(trades);
		Mockito.when(aggTradeRepository.findDistinctSymbols()).thenReturn(symbols);
		Mockito.when(aggTradeRepository.findBySymbolAndProfitIsNull(symbols.get(0))).thenReturn(trades);

		profitService.saveAllTradeProfits();

		List<Profit> profits = profitService.listAllProfits();

		Assert.assertTrue(!profits.isEmpty());
		Assert.assertEquals(1, profits.size());
		Assert.assertEquals(new BigDecimal("55"), profits.get(0).getQuantity());
		Assert.assertEquals(BaseCurrency.ETH, profits.get(0).getBaseCurrency());
		Assert.assertEquals(new BigDecimal("15"), profits.get(0).getProfitValue());
	}

	@Test
	@Transactional
	public void saveUnclearSellTradeWithBuyListTest() throws Exception {
		List<AggregatedTrade> trades = new ArrayList<>();
		List<String> symbols = new ArrayList<>();
		symbols.add("ETHBTC");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String sellDateString = "27-02-2018 10:20:56";
		String dateBuy1String = "15-02-2018 11:21:56";
		String dateBuy2String = "24-02-2018 12:31:56";
		String dateBuy3String = "25-02-2018 13:51:56";

		Date sellDate = formatter.parse(sellDateString);
		Date buyDate1 = formatter.parse(dateBuy1String);
		Date buyDate2 = formatter.parse(dateBuy2String);
		Date buyDate3 = formatter.parse(dateBuy3String);

		AggregatedTrade buy1 = new AggregatedTrade();
		buy1.setBuy(true);
		buy1.setQuantity(new BigDecimal("1"));
		buy1.setPrice(new BigDecimal("1"));
		buy1.setTotal(new BigDecimal("10"));
		buy1.setFee(new BigDecimal("0.1"));
		buy1.setFeeCoin("BNB");
		buy1.setSymbol("LSKETH");
		buy1.setTradeTime(buyDate1);
		trades.add(buy1);

		AggregatedTrade buy2 = new AggregatedTrade();
		buy2.setBuy(true);
		buy2.setQuantity(new BigDecimal("1"));
		buy2.setPrice(new BigDecimal("1"));
		buy2.setTotal(new BigDecimal("10"));
		buy2.setFee(new BigDecimal("0.1"));
		buy2.setFeeCoin("BNB");
		buy2.setSymbol("LSKETH");
		buy2.setTradeTime(buyDate2);
		trades.add(buy2);

		AggregatedTrade buy3 = new AggregatedTrade();
		buy3.setBuy(true);
		buy3.setQuantity(new BigDecimal("1"));
		buy3.setPrice(new BigDecimal("1"));
		buy3.setTotal(new BigDecimal("10"));
		buy3.setFee(new BigDecimal("0.1"));
		buy3.setFeeCoin("BNB");
		buy3.setSymbol("LSKETH");
		buy3.setTradeTime(buyDate3);
		trades.add(buy3);

		AggregatedTrade buy4 = new AggregatedTrade();
		buy4.setBuy(true);
		buy4.setQuantity(new BigDecimal("1"));
		buy4.setPrice(new BigDecimal("1"));
		buy4.setTotal(new BigDecimal("10"));
		buy4.setFee(new BigDecimal("0.1"));
		buy4.setFeeCoin("BNB");
		buy4.setSymbol("LSKETH");
		buy4.setTradeTime(buyDate3);
		trades.add(buy4);

		AggregatedTrade sell = new AggregatedTrade();
		sell.setBuy(false);
		sell.setQuantity(new BigDecimal("4"));
		sell.setPrice(new BigDecimal("2"));
		sell.setTotal(new BigDecimal("55"));
		sell.setFee(new BigDecimal("0.1"));
		sell.setFeeCoin("BNB");
		sell.setSymbol("LSKETH");
		sell.setTradeTime(sellDate);
		trades.add(sell);

		Mockito.when(aggTradeService.listAllAggregated()).thenReturn(trades);
		Mockito.when(aggTradeRepository.findDistinctSymbols()).thenReturn(symbols);
		Mockito.when(aggTradeRepository.findBySymbolAndProfitIsNull(symbols.get(0))).thenReturn(trades);

		profitService.saveAllTradeProfits();

		List<Profit> profits = profitService.listAllProfits();

		Assert.assertTrue(!profits.isEmpty());
		Assert.assertEquals(1, profits.size());
		Assert.assertEquals(new BigDecimal("4"), profits.get(0).getQuantity());
		Assert.assertEquals(BaseCurrency.ETH, profits.get(0).getBaseCurrency());
		Assert.assertEquals(new BigDecimal("15"), profits.get(0).getProfitValue());
		Assert.assertEquals(new BigDecimal("4"), profits.get(0).getPriceDifference());
	}

}
