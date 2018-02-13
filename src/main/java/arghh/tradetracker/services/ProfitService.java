package arghh.tradetracker.services;

import java.util.List;

import arghh.tradetracker.commands.ProfitList;
import arghh.tradetracker.model.Profit;

public interface ProfitService {

	void saveAllTradeProfits();

	List<ProfitList> listDailyTradeProfits(String date);

	List<String> totalDailyProfits(String date);

	List<Profit> listAllProfits();

	List<ProfitList> showAllProfits();

	List<String> totalProfits();

	Profit saveOrUpdate(Profit profit);

	void deleteAll();

}
