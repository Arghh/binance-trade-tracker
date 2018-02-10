package profittracker.services;

import java.util.List;

import profittracker.domain.AggregatedTrade;
import profittracker.domain.Trade;

public interface AggregatedTradeService {

	List<AggregatedTrade> listAllAggregated();

	AggregatedTrade getById(Long id);

	AggregatedTrade saveOrUpdate(AggregatedTrade trade);

	void delete(Long id);

	void convertAndSaveAllAggTrades(List<Trade> trades);

}
