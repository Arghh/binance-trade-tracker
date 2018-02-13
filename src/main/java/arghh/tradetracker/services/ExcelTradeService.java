package arghh.tradetracker.services;

import java.util.List;

import arghh.tradetracker.model.Trade;

public interface ExcelTradeService {

	List<Trade> saveAllTradesFromFile();

	public Trade saveNewExcelTrade(Trade trade);
}
