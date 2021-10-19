package arghh.tradetracker.converters;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import arghh.tradetracker.model.AggregatedTrade;
import arghh.tradetracker.model.Trade;

@Component
public class TradeToAggTrade implements Converter<Trade, AggregatedTrade> {

    @Override
    public AggregatedTrade convert(Trade trade) {
	List<Trade> tradesToSave = new ArrayList<>();
	tradesToSave.add(trade);

	var aggTrade = new AggregatedTrade();
	aggTrade.setTradeTime(trade.getTradeTime());
	aggTrade.setSymbol(trade.getSymbol());
	aggTrade.setBuy(trade.isBuy());
	aggTrade.setPrice(trade.getPrice());
	aggTrade.setQuantity(trade.getQuantity());
	aggTrade.setTotal(trade.getTotal());
	aggTrade.setFee(trade.getFee());
	aggTrade.setFeeCoin(trade.getFeeCoin());
	aggTrade.setTrade(tradesToSave);

	return aggTrade;

    }
}
