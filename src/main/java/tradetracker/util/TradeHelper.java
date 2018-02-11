package tradetracker.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import tradetracker.exception.ProfitException;
import tradetracker.services.BaseCurrency;

public class TradeHelper {

	public static BigDecimal addBigDecimals(List<BigDecimal> decimals) {
		return decimals.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	public static BigDecimal substractBigDecimals(BigDecimal buy, BigDecimal sell) {
		return sell.subtract(buy);
	}

	public static BaseCurrency getBaseCurrency(String symbol) throws ProfitException {
		if (symbol.length() != 6) {
			System.out.println("The market symbol " + symbol + " is invalid");
			throw new ProfitException("The market symbol " + symbol + " is invalid");
		}
		try {
			String currency = symbol.substring(3, symbol.length());
			return BaseCurrency.valueOf(currency);
		} catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
		}
		return null;

	}

	public static Date getTimeBetweenTrades(Date buy, Date sell) throws ProfitException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
		String buyTime = formatter.format(buy.getTime());
		String sellTime = formatter.format(sell.getTime());

		if (buy.after(sell)) {
			throw new ProfitException("Buy time " + buyTime + " can't come after sell time " + sellTime);
		}

		return new Date(sell.getTime() - buy.getTime());
	}
}
