package profittracker.services;

import java.util.List;

import com.webcerebrium.binance.datatype.BinanceTrade;

import profittracker.domain.Trade;
import profittracker.exception.TradeException;

/**
 * Created by jt on 1/10/17.
 */
public interface BinanceApiTradeService {

	Trade saveNewBinanceTrade(BinanceTrade productForm) throws TradeException;

	public List<Trade> saveAllBinanaceTrades(String symbol);
}
