package profittracker.services;

import java.util.List;

import profittracker.domain.AggregatedTrade;

public interface ProfitService {

	List<AggregatedTrade> listAllTradeProfits();

	List<AggregatedTrade> listDailyTradeProfits();

}
