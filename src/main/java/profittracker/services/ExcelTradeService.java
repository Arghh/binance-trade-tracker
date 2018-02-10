package profittracker.services;

import java.util.List;

import profittracker.domain.Trade;

/**
 * Created by jt on 1/10/17.
 */
public interface ExcelTradeService {

	List<Trade> saveAllTradesFromFile();

	public Trade saveNewExcelTrade(List<String> data);
}
