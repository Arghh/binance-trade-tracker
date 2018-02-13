package arghh.tradetracker.converters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import arghh.tradetracker.model.AggregatedTrade;
import arghh.tradetracker.model.Trade;
import arghh.tradetracker.repositories.TradeRepository;

@Component
public class TradeToAggTrade implements Converter<Trade, AggregatedTrade> {

	private TradeRepository tradeRepository;

	@Autowired
	public TradeToAggTrade(TradeRepository tradeRepository) {
		this.tradeRepository = tradeRepository;
	}

	@Override
	public AggregatedTrade convert(Trade trade) {

		AggregatedTrade aggTrade = new AggregatedTrade();
		aggTrade.setTradeTime(trade.getTradeTime());
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
