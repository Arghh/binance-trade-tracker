package tradetracker.services;

import java.util.List;

import com.webcerebrium.binance.datatype.BinanceTrade;

import tradetracker.exception.TradeException;
import tradetracker.model.Trade;

/**
 * Created by jt on 1/10/17.
 */
public interface BinanceApiTradeService {

	Trade saveNewBinanceTrade(BinanceTrade productForm) throws TradeException;

	public List<Trade> saveAllBinanaceTrades(String symbol);
}
