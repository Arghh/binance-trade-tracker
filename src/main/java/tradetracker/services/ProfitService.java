package tradetracker.services;

import java.util.List;

import tradetracker.model.AggregatedTrade;
import tradetracker.model.Profit;

public interface ProfitService {

	void saveAllTradeProfits();

	List<AggregatedTrade> listDailyTradeProfits();

	List<Profit> listAllProfits();

	Profit saveOrUpdate(Profit profit);

}
