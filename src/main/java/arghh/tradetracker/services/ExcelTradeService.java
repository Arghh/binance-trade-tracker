package arghh.tradetracker.services;

import java.util.List;

import arghh.tradetracker.model.Trade;

public interface ExcelTradeService {

    List<Trade> saveAllTradesFromFile();

    Trade saveNewExcelTrade(Trade trade);
}
