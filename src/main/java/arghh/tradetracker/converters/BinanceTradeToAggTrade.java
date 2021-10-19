package arghh.tradetracker.converters;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.webcerebrium.binance.datatype.BinanceTrade;

import arghh.tradetracker.exception.TradeException;
import arghh.tradetracker.model.AggregatedTrade;
import arghh.tradetracker.repositories.AggregatedTradeRepository;

@Component
public class BinanceTradeToAggTrade implements Converter<BinanceTrade, AggregatedTrade> {
    private AggregatedTradeRepository tradeRepository;

    @Autowired
    public BinanceTradeToAggTrade(AggregatedTradeRepository tradeRepository) {
	this.tradeRepository = tradeRepository;
    }

    @Override
    public AggregatedTrade convert(BinanceTrade binanceTrade) {
	AggregatedTrade trade = null;
	try {
	    checkIfTradeExists(binanceTrade);
	    trade = new AggregatedTrade();
	    trade.setPrice(binanceTrade.getPrice());
	    trade.setFeeCoin(binanceTrade.getCommissionAsset());
	    trade.setBinanceId(binanceTrade.getId());
	    trade.setFee(binanceTrade.getCommission());
	    trade.setTradeTime(new Date(binanceTrade.getTime()));
	    trade.setQuantity(binanceTrade.getQty());
	    trade.setBuy(binanceTrade.isBuyer);
	    return trade;

	} catch (TradeException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return null;
    }

    private TradeException checkIfTradeExists(BinanceTrade newTrade) throws TradeException {

	var oldTrade = tradeRepository.findByBinanceId(newTrade.getId());
	if (oldTrade != null) {
	    throw new TradeException("The Trade with the ID " + oldTrade.getId() + " and Binance ID " + newTrade.getId()
		    + " already exists.");

	}

	return null;
    }
}
