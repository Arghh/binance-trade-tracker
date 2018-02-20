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
			List<AggregatedTrade> buys = trades.stream().filter(x -> x.isBuy()).collect(Collectors.toList());
			List<AggregatedTrade> sells = trades.stream().filter(x -> !x.isBuy()).collect(Collectors.toList());

			if (buys.isEmpty() || sells.isEmpty()) {
				return null;
			}
			Profit profit = new Profit();
			AggregatedTrade firstBuy = buys.get(0);
			AggregatedTrade lastSell = sells.get(sells.size() - 1);
			List<BigDecimal> allQuantities = new ArrayList<>();
			List<BigDecimal> allCosts = new ArrayList<>();
			List<BigDecimal> allFees = new ArrayList<>();

			profit.setBaseCurrency(TradeHelper.getBaseCurrency(firstBuy.getSymbol()));
			profit.setSellTime(lastSell.getTradeTime());
			profit.setTimeDifference(TradeHelper.getMsBetweenTrades(firstBuy.getTradeTime(), lastSell.getTradeTime()));

			// depending on if there is 1 buy or 1 sell order we will the rest to convert to
			// profit TODO: refactor
			if (buys.size() == 1) {
				profit.setQuantity(firstBuy.getQuantity());

				sells.forEach(s -> {
					allCosts.add(s.getTotal());
					allQuantities.add(s.getQuantity());
					allFees.add(s.getFee());
					s.setProfit(profit);
				});
				BigDecimal totalCost = TradeHelper.addBigDecimals(allCosts);
				profit.setProfitValue(TradeHelper.substractBigDecimals(firstBuy.getTotal(), totalCost));
				// TODO: price difference need to be avg of all sells or buys?
				profit.setPriceDifference(TradeHelper.substractBigDecimals(firstBuy.getPrice(), lastSell.getPrice()));

			} else {
				profit.setQuantity(lastSell.getQuantity());

				buys.forEach(b -> {
					allCosts.add(b.getTotal());
					allQuantities.add(b.getQuantity());
					allFees.add(b.getFee());
					b.setProfit(profit);
				});

				profit.setbuySellPair(trades);
				BigDecimal totalCost = TradeHelper.addBigDecimals(allCosts);
				profit.setProfitValue(TradeHelper.substractBigDecimals(totalCost, lastSell.getTotal()));
				// TODO: price difference need to be avg of all sells or buys?
				profit.setPriceDifference(TradeHelper.substractBigDecimals(firstBuy.getPrice(), lastSell.getPrice()));
			}

			return profit;
		} catch (ProfitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

}
