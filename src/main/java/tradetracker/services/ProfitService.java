package tradetracker.services;

import java.math.BigDecimal;
import java.util.List;

import tradetracker.commands.ProfitList;
import tradetracker.model.Profit;

public interface ProfitService {

	void saveAllTradeProfits();

	List<Profit> listDailyTradeProfits();

	List<Profit> listAllProfits();

	List<ProfitList> showAllProfits();

	BigDecimal totalProfits();

	Profit saveOrUpdate(Profit profit);

}
