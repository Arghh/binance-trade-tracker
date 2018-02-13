package arghh.tradetracker.services;

import java.util.List;

import com.webcerebrium.binance.datatype.BinanceTrade;

import arghh.tradetracker.exception.TradeException;
import arghh.tradetracker.model.Trade;

public interface BinanceApiTradeService {

	Trade saveNewBinanceTrade(BinanceTrade productForm) throws TradeException;

	public List<Trade> saveAllBinanaceTrades(String symbol);
}
