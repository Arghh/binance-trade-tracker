package arghh.tradetracker.services;

import java.util.List;

import arghh.tradetracker.model.AggregatedTrade;
import arghh.tradetracker.model.Trade;

public interface AggregatedTradeService {

	List<AggregatedTrade> listAllAggregated();

	AggregatedTrade getById(Long id);

	AggregatedTrade saveOrUpdate(AggregatedTrade trade);

	void delete(Long id);

	void convertAndSaveAllAggTrades(List<Trade> trades);

	void deleteAll();

}
