package profittracker.services;

import java.util.List;

import profittracker.domain.Trade;

public interface TradeService {

	List<Trade> listAllRaw();

	void saveAllAggregated();

	Trade getById(Long id);

	Trade saveOrUpdate(Trade trade);

	void delete(Long id);

}
