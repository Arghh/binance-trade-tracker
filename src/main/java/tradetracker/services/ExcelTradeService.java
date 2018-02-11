package tradetracker.services;

import java.util.List;

import tradetracker.model.Trade;

/**
 * Created by jt on 1/10/17.
 */
public interface ExcelTradeService {

	List<Trade> saveAllTradesFromFile();

	public Trade saveNewExcelTrade(Trade trade);
}
