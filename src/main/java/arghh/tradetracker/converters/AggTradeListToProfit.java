package arghh.tradetracker.converters;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import arghh.tradetracker.exception.ProfitException;
import arghh.tradetracker.model.AggregatedTrade;
import arghh.tradetracker.model.Profit;
import arghh.tradetracker.util.TradeHelper;

@Component
public class AggTradeListToProfit implements Converter<List<AggregatedTrade>, Profit> {

    @Override
    public Profit convert(List<AggregatedTrade> trades) {

	try {
	    // double check
	    List<AggregatedTrade> buys = trades.stream().filter(AggregatedTrade::isBuy).collect(Collectors.toList());
	    List<AggregatedTrade> sells = trades.stream().filter(x -> !x.isBuy()).collect(Collectors.toList());

	    if (buys.isEmpty() || sells.isEmpty()) {
		return null;
	    }

	    var profit = new Profit();
	    var firstBuy = buys.get(0);
	    var lastSell = sells.get(sells.size() - 1);
	    List<BigDecimal> allCosts = new ArrayList<>();

	    profit.setBaseCurrency(TradeHelper.getBaseCurrency(firstBuy.getSymbol()));
	    profit.setSellTime(lastSell.getTradeTime());
	    profit.setTimeDifference(TradeHelper.getMsBetweenTrades(firstBuy.getTradeTime(), lastSell.getTradeTime()));

	    // depending on if there is 1 buy or 1 sell order we will the rest to convert to
	    // profit TODO: refactor
	    if (buys.size() == 1) {
		profit.setQuantity(firstBuy.getQuantity());

		sells.forEach(s -> {
		    allCosts.add(s.getTotal());
		    s.setProfit(profit);
		});
		buys.get(0).setProfit(profit);
		profit.setbuySellPair(trades);
		var totalCost = TradeHelper.addBigDecimals(allCosts);
		profit.setProfitValue(TradeHelper.substractBigDecimals(firstBuy.getTotal(), totalCost));
		var priceDifference = calculatePriceBasedOnListValues(firstBuy, sells);
		// TODO: price difference need to be avg of all sells or buys?
		profit.setPriceDifference(priceDifference);

	    } else {
		profit.setQuantity(lastSell.getQuantity());

		buys.forEach(b -> {
		    allCosts.add(b.getTotal());
		    b.setProfit(profit);
		});
		sells.get(0).setProfit(profit);
		profit.setbuySellPair(trades);
		var totalCost = TradeHelper.addBigDecimals(allCosts);
		profit.setProfitValue(TradeHelper.substractBigDecimals(totalCost, lastSell.getTotal()));
		var priceDifference = calculatePriceBasedOnListValues(lastSell, buys);
		profit.setPriceDifference(priceDifference);

		// TODO: price difference need to be avg of all sells or buys?
		// profit.setPriceDifference(TradeHelper.substractBigDecimals(firstBuy.getPrice(),
		// lastSell.getPrice()));
	    }

	    return profit;
	} catch (ProfitException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	return null;
    }

    private BigDecimal calculatePriceBasedOnListValues(AggregatedTrade one, List<AggregatedTrade> many) {

	// total price * quantity
	var x = one.getQuantity().multiply(one.getPrice());

	// part prices with there quantities as weight
	var y = new BigDecimal("0");
	for (AggregatedTrade aggregatedTrade : many) {
	    y = y.add(aggregatedTrade.getQuantity().multiply(aggregatedTrade.getPrice()));
	}

	return x.subtract(y);
    }

}
