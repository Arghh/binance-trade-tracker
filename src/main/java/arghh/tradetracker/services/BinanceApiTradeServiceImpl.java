package arghh.tradetracker.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.webcerebrium.binance.api.BinanceApiException;
import com.webcerebrium.binance.datatype.BinanceSymbol;
import com.webcerebrium.binance.datatype.BinanceTrade;

import arghh.tradetracker.converters.BinanceTradeToTrade;
import arghh.tradetracker.model.Trade;

@Service
public class BinanceApiTradeServiceImpl implements BinanceApiTradeService {

	private TradeService tradeService;
	private BinanceTradeToTrade tradeConverter;
	// private BinanceApi api = new BinanceApi();

	@Value("${tradeHistory.path}")
	private String filePath;

	@Autowired
	public BinanceApiTradeServiceImpl(TradeService tradeService, BinanceTradeToTrade tradeConverter) {
		this.tradeService = tradeService;
		this.tradeConverter = tradeConverter;
	}

	@Override
	public Trade saveNewBinanceTrade(BinanceTrade newTrade) {

		Trade savedTrade = tradeConverter.convert(newTrade);
		if (savedTrade != null) {
			tradeService.saveOrUpdate(savedTrade);
			System.out.println("Saved a trade with Binance Id: " + savedTrade.getBinanceId());
		} else {
			System.out.println("Could not save a new trade");
		}

		return savedTrade;
	}

	@Override
	public List<Trade> saveAllBinanaceTrades(String symbol) {
		try {
			BinanceSymbol sy = new BinanceSymbol(symbol);
			List<BinanceTrade> trades = new ArrayList<>();
			// List<BinanceTrade> trades = api.myTrades(sy);
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
