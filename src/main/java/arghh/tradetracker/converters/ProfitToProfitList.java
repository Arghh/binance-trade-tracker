package arghh.tradetracker.converters;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import arghh.tradetracker.commands.ProfitList;
import arghh.tradetracker.model.AggregatedTrade;
import arghh.tradetracker.model.Profit;
import arghh.tradetracker.util.TradeHelper;

@Component
public class ProfitToProfitList implements Converter<List<Profit>, List<ProfitList>> {

	@Override
	public List<ProfitList> convert(List<Profit> profitsForSymbol) {

		if (profitsForSymbol.isEmpty()) {
			return new ArrayList<>();
		}

		List<ProfitList> profits = new ArrayList<>();

		for (Profit data : profitsForSymbol) {
			ProfitList profit = new ProfitList();
			AggregatedTrade buy = new AggregatedTrade();
			AggregatedTrade sell = new AggregatedTrade();
			if (data.getbuySellPair().size() > 1) {
				buy = data.getbuySellPair().get(0);
				sell = data.getbuySellPair().get(1);
				profit.setId(data.getId());
				profit.setSymbol(buy.getSymbol());
				profit.setBuyPrice(buy.getPrice());
				profit.setSellPrice(sell.getPrice());
				profit.setProfit(TradeHelper.addBaseCurrencyProfit(data.getProfitValue(), data.getBaseCurrency()));
				profit.setQuantity(TradeHelper.addStringToBigDecimal(data.getQuantity(), buy.getSymbol()));
				profit.setBuyTime(buy.getTradeTime());
				profit.setSellTime(sell.getTradeTime());
				profit.setTimeDifference(TradeHelper.getTimeDifference(buy.getTradeTime(), sell.getTradeTime()));

				profits.add(profit);
			} else {
				System.out.println("profit " + profit.getId() + " has lost he's buy or sell trade.");
			}
		}

		return profits;

	}

}
