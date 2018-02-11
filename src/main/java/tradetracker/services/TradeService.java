package tradetracker.services;

import java.util.List;

import tradetracker.model.Trade;

public interface TradeService {

	List<Trade> listAllRaw();

	void saveAllAggregatedTrades();

	Trade getById(Long id);

	Trade saveOrUpdate(Trade trade);

	void delete(Long id);

}
