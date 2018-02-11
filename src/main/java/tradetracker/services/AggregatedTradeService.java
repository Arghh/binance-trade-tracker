package tradetracker.services;

import java.util.List;

import tradetracker.model.AggregatedTrade;
import tradetracker.model.Trade;

public interface AggregatedTradeService {

	List<AggregatedTrade> listAllAggregated();

	AggregatedTrade getById(Long id);

	AggregatedTrade saveOrUpdate(AggregatedTrade trade);

	void delete(Long id);

	void convertAndSaveAllAggTrades(List<Trade> trades);

}
