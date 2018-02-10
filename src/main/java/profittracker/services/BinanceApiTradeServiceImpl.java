package profittracker.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.webcerebrium.binance.api.BinanceApi;
import com.webcerebrium.binance.api.BinanceApiException;
import com.webcerebrium.binance.datatype.BinanceSymbol;
import com.webcerebrium.binance.datatype.BinanceTrade;

import profittracker.converters.BinanceTradeToTrade;
import profittracker.domain.Trade;

@Service
public class BinanceApiTradeServiceImpl implements BinanceApiTradeService {

	private TradeService tradeService;
	private BinanceTradeToTrade productFormToProduct;
	private BinanceApi api;

	@Value("${tradeHistory.path}")
	private String filePath;

	@Autowired
	public BinanceApiTradeServiceImpl(TradeService tradeService, BinanceTradeToTrade productFormToProduct) {
		this.tradeService = tradeService;
		this.productFormToProduct = productFormToProduct;
		this.api = new BinanceApi();
	}

	@Override
	public Trade saveNewBinanceTrade(BinanceTrade newTrade) {

		Trade savedTrade = productFormToProduct.convert(newTrade);
		if (savedTrade != null) {
			tradeService.saveOrUpdate(savedTrade);
			System.out.println("Saved Trade with Binance Id: " + savedTrade.getBinanceId());
		} else {
			System.out.println("Could not save a new trade");
		}

		return savedTrade;
	}

	@Override
	public List<Trade> saveAllBinanaceTrades(String symbol) {
		try {
			BinanceSymbol sy = new BinanceSymbol(symbol);
			List<BinanceTrade> trades = api.myTrades(sy);
			System.out.println(trades);
			for (BinanceTrade binanceTrade : trades) {
				saveNewBinanceTrade(binanceTrade);
			}
		} catch (BinanceApiException e) {
			System.out.println(e.getMessage());
		}
		return new ArrayList<Trade>();
	}

}
