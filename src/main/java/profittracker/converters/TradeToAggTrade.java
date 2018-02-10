package profittracker.converters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import profittracker.domain.AggregatedTrade;
import profittracker.domain.Trade;
import profittracker.repositories.TradeRepository;

@Component
public class TradeToAggTrade implements Converter<Trade, AggregatedTrade> {

	private TradeRepository tradeRepository;

	@Autowired
	public TradeToAggTrade(TradeRepository tradeRepository) {
		this.tradeRepository = tradeRepository;
	}

	@Override
	public AggregatedTrade convert(Trade trade) {
		// if (source.size() != 8) {
		// System.out.println("To many cells in " + source + " this row.");
		// return null;
		// }

		AggregatedTrade aggTrade = new AggregatedTrade();
		aggTrade.setTimeOfTrade(trade.getTimeOfTrade());
		aggTrade.setSymbol(trade.getSymbol());
		aggTrade.setBuy(trade.isBuy());
		aggTrade.setPrice(trade.getPrice());
		aggTrade.setQuantity(trade.getQuantity());
		aggTrade.setTotal(trade.getTotal());
		aggTrade.setFee(trade.getFee());
		aggTrade.setFeeCoin(trade.getFeeCoin());

		return aggTrade;

	}
}
