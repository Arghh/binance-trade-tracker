package arghh.tradetracker.converters;

import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import arghh.tradetracker.exception.ProfitException;
import arghh.tradetracker.model.AggregatedTrade;
import arghh.tradetracker.model.Profit;
import arghh.tradetracker.util.TradeHelper;

@Component
public class AggTradeToProfit implements Converter<List<AggregatedTrade>, Profit> {

    @Override
    public Profit convert(List<AggregatedTrade> buySellPair) {

	try {
	    // double checking
	    if (!buySellPair.get(0).isBuy() || buySellPair.get(1).isBuy()) {
		return null;
	    }

	    var profit = new Profit();
	    var buy = buySellPair.get(0);
	    var sell = buySellPair.get(1);

	    profit.setBaseCurrency(TradeHelper.getBaseCurrency(buy.getSymbol()));
	    profit.setQuantity(buy.getQuantity());
	    profit.setProfitValue(TradeHelper.substractBigDecimals(buy.getTotal(), sell.getTotal()));
	    profit.setPriceDifference(TradeHelper.substractBigDecimals(buy.getPrice(), sell.getPrice()));
	    if (sell.getTradeTime() != null) {
		profit.setSellTime(sell.getTradeTime());
	    }
	    profit.setTimeDifference(TradeHelper.getMsBetweenTrades(buy.getTradeTime(), sell.getTradeTime()));
	    buy.setProfit(profit);
	    sell.setProfit(profit);
	    profit.setbuySellPair(buySellPair);
	    return profit;
	} catch (ProfitException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	return null;

    }
}
