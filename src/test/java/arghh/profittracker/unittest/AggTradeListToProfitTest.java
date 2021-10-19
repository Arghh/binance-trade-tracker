package arghh.profittracker.unittest;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import arghh.tradetracker.converters.AggTradeListToProfit;
import arghh.tradetracker.model.AggregatedTrade;
import arghh.tradetracker.model.Profit;
import arghh.tradetracker.services.BaseCurrency;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { AggTradeListToProfit.class })
@ActiveProfiles("test")
public class AggTradeListToProfitTest {

    @Autowired
    private AggTradeListToProfit profitConverter;

    @Test
    public void aggTradeListToProfitWithSellListTest() throws Exception {
	List<AggregatedTrade> trades = new ArrayList<>();
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	String dateBuyString = "22-01-2018 10:20:56";
	String dateSellString1 = "23-01-2018 11:21:56";
	String dateSellString2 = "24-01-2018 12:31:56";
	String dateSellString3 = "25-01-2018 13:47:56";

	Date buyDate = formatter.parse(dateBuyString);
	Date sellDate1 = formatter.parse(dateSellString1);
	Date sellDate2 = formatter.parse(dateSellString2);
	Date sellDate3 = formatter.parse(dateSellString3);

	AggregatedTrade buy = new AggregatedTrade();
	buy.setId(5L);
	buy.setBuy(true);
	buy.setQuantity(new BigDecimal("9.3"));
	buy.setSymbol("LTCBTC");
	buy.setPrice(new BigDecimal("0.01987712"));
	buy.setTotal(new BigDecimal("0.18485721"));
	buy.setTradeTime(buyDate);
	trades.add(buy);

	AggregatedTrade sell1 = new AggregatedTrade();
	sell1.setId(1L);
	sell1.setBuy(false);
	sell1.setQuantity(new BigDecimal("5"));
	sell1.setSymbol("LTCBTC");
	sell1.setPrice(new BigDecimal("0.02500000"));
	sell1.setTotal(new BigDecimal("0.09938560"));
	sell1.setTradeTime(sellDate1);
	trades.add(sell1);

	AggregatedTrade sell2 = new AggregatedTrade();
	sell2.setId(2L);
	sell2.setBuy(false);
	sell2.setQuantity(new BigDecimal("2"));
	sell2.setSymbol("LTCBTC");
	sell2.setPrice(new BigDecimal("0.01500000"));
	sell2.setTotal(new BigDecimal("0.03975424"));
	sell2.setTradeTime(sellDate2);
	trades.add(sell2);

	AggregatedTrade sell3 = new AggregatedTrade();
	sell3.setId(3L);
	sell3.setBuy(false);
	sell3.setQuantity(new BigDecimal("2.3"));
	sell3.setSymbol("LTCBTC");
	sell3.setPrice(new BigDecimal("0.01900000"));
	sell3.setTotal(new BigDecimal("0.04975424"));
	sell3.setTradeTime(sellDate3);
	trades.add(sell3);

	Profit profit = profitConverter.convert(trades);

	Assertions.assertTrue(profit != null);
	Assertions.assertEquals(new BigDecimal("9.3"), profit.getQuantity());
	Assertions.assertEquals(BaseCurrency.BTC, profit.getBaseCurrency());
	Assertions.assertEquals(new BigDecimal("0.00403687"), profit.getProfitValue());
	Assertions.assertEquals(new BigDecimal("-0.013842784"), profit.getPriceDifference());
	Assertions.assertEquals(sell3.getTradeTime().getTime() - buy.getTradeTime().getTime(),
		profit.getTimeDifference());
    }

    @Test
    public void aggTradeListToProfitWithBuyListTest() throws Exception {
	List<AggregatedTrade> trades = new ArrayList<>();
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	String dateSellString = "29-01-2018 10:20:56";
	String dateBuy1String = "23-01-2018 11:25:56";
	String dateBuy2String = "24-01-2018 12:26:56";
	String dateBuy3String = "25-01-2018 13:27:56";

	Date sellDate = formatter.parse(dateSellString);
	Date buyDate1 = formatter.parse(dateBuy1String);
	Date buyDate2 = formatter.parse(dateBuy2String);
	Date buyDate3 = formatter.parse(dateBuy3String);

	AggregatedTrade sell = new AggregatedTrade();
	sell.setId(5L);
	sell.setBuy(false);
	sell.setQuantity(new BigDecimal("5"));
	sell.setSymbol("LTCBTC");
	sell.setPrice(new BigDecimal("10"));
	sell.setTotal(new BigDecimal("12"));
	sell.setTradeTime(sellDate);
	trades.add(sell);

	AggregatedTrade buy1 = new AggregatedTrade();
	buy1.setId(1L);
	buy1.setBuy(true);
	buy1.setQuantity(new BigDecimal("1"));
	buy1.setSymbol("LTCBTC");
	buy1.setPrice(new BigDecimal("5"));
	buy1.setTotal(new BigDecimal("7"));
	buy1.setTradeTime(buyDate1);
	trades.add(buy1);

	AggregatedTrade buy2 = new AggregatedTrade();
	buy2.setId(2L);
	buy2.setBuy(true);
	buy2.setQuantity(new BigDecimal("1"));
	buy2.setSymbol("LTCBTC");
	buy2.setPrice(new BigDecimal("5"));
	buy2.setTotal(new BigDecimal("7"));
	buy2.setTradeTime(buyDate2);
	trades.add(buy2);

	AggregatedTrade buy3 = new AggregatedTrade();
	buy3.setId(3L);
	buy3.setBuy(true);
	buy3.setQuantity(new BigDecimal("3"));
	buy3.setSymbol("LTCBTC");
	buy3.setPrice(new BigDecimal("8"));
	buy3.setTotal(new BigDecimal("11"));
	buy3.setTradeTime(buyDate3);
	trades.add(buy3);

	Profit profit = profitConverter.convert(trades);

	Assertions.assertTrue(profit != null);
	Assertions.assertEquals(new BigDecimal("5"), profit.getQuantity());
	Assertions.assertEquals(BaseCurrency.BTC, profit.getBaseCurrency());
	Assertions.assertEquals(new BigDecimal("16"), profit.getPriceDifference());
	Assertions.assertEquals(new BigDecimal("-13"), profit.getProfitValue());
	Assertions.assertEquals(sell.getTradeTime().getTime() - buy1.getTradeTime().getTime(),
		profit.getTimeDifference());
    }

}
