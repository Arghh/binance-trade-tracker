package arghh.tradetracker.services;

import java.util.List;

import arghh.tradetracker.model.AggregatedTrade;

public interface AggregatedTradeService {

	List<AggregatedTrade> listAllAggregated();

	AggregatedTrade getById(Long id);

	AggregatedTrade saveOrUpdate(AggregatedTrade trade);

	void delete(Long id);

	void saveAllAggTrades(List<AggregatedTrade> trades);

	void deleteAll();

}
