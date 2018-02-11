package tradetracker.converters;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import tradetracker.commands.ProfitList;
import tradetracker.model.AggregatedTrade;
import tradetracker.model.Profit;
import tradetracker.repositories.ProfitRepository;
import tradetracker.services.BaseCurrency;
import tradetracker.util.TradeHelper;

@Component
public class ProfitToProfitList implements Converter<List<Profit>, List<ProfitList>> {

	private ProfitRepository tradeRepository;

	@Autowired
	public ProfitToProfitList(ProfitRepository tradeRepository) {
		this.tradeRepository = tradeRepository;
	}

	@Override
	public List<ProfitList> convert(List<Profit> profitsForSymbol) {

		if (profitsForSymbol.isEmpty()) {
			return new ArrayList<>();
		}

		List<ProfitList> profits = new ArrayList<>();

		for (Profit data : profitsForSymbol) {
			ProfitList profit = new ProfitList();
			AggregatedTrade buy = data.getbuySellPair().get(0);
			AggregatedTrade sell = data.getbuySellPair().get(1);
			profit.setSymbol(buy.getSymbol());
			profit.setBuyPrice(buy.getPrice());
			profit.setSellPrice(sell.getPrice());
			profit.setProfit(addBaseCurrencyProfit(data.getProfitValue(), data.getBaseCurrency()));
			profit.setQuantity(data.getQuantity());
			profit.setBuyTime(buy.getTradeTime());
			profit.setSellTime(sell.getTradeTime());
			// profit.setTimeDifference(TradeHelper.getTimeDifference(buy.getTradeTime(),
			// sell.getTradeTime()));
			profit.setTimeDifference(TradeHelper.getTimeDifference(buy.getTradeTime(), sell.getTradeTime()));
			profits.add(profit);
		}

		return profits;

	}

	private String addBaseCurrencyProfit(BigDecimal profit, BaseCurrency baseCurrency) {

		return profit.toString() + " " + baseCurrency.toString();

	}
}
