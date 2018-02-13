package arghh.tradetracker.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import arghh.tradetracker.exception.ProfitException;
import arghh.tradetracker.services.BaseCurrency;

public class TradeHelper {

	public static BigDecimal addBigDecimals(List<BigDecimal> decimals) {
		return decimals.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	public static BigDecimal substractBigDecimals(BigDecimal buy, BigDecimal sell) {
		return sell.subtract(buy);
	}

	public static BaseCurrency getBaseCurrency(String symbol) throws ProfitException {
		String currency = symbol.substring(symbol.length() - 3);
		// if (symbol.length() != 6) {
		// System.out.println("The market symbol " + symbol + " is invalid");
		// throw new ProfitException("The market symbol " + symbol + " is invalid");
		// }
		try {
			return BaseCurrency.valueOf(currency);
		} catch (IllegalArgumentException e) {
			System.out.println("The market symbol " + symbol + " is invalid");
		}
		return null;

	}

	public static long getMsBetweenTrades(Date buy, Date sell) throws ProfitException {
		SimpleDateFormat formatter = new SimpleDateFormat("dd hh:mm:ss");
		String buyTime = formatter.format(buy.getTime());
		String sellTime = formatter.format(sell.getTime());

		if (buy.after(sell)) {
			throw new ProfitException("Buy time " + buyTime + " can't come after sell time " + sellTime);
		}

		return sell.getTime() - buy.getTime();
	}

	// 1 minute = 60 seconds
	// 1 hour = 60 x 60 = 3600
	// 1 day = 3600 x 24 = 86400
	public static String getTimeDifference(Date buy, Date sell) {

		StringBuilder sb = new StringBuilder();
		// milliseconds
		long dif = sell.getTime() - buy.getTime();

		long secondsInMilli = 1000;
		long minutesInMilli = secondsInMilli * 60;
		long hoursInMilli = minutesInMilli * 60;
		long daysInMilli = hoursInMilli * 24;

		long elapsedDays = dif / daysInMilli;
		if (elapsedDays != 0) {
			sb.append(elapsedDays + " days, ");
		}
		dif = dif % daysInMilli;

		long elapsedHours = dif / hoursInMilli;
		if (elapsedHours != 0) {
			sb.append(elapsedHours + "h ");
		}
		dif = dif % hoursInMilli;

		long elapsedMinutes = dif / minutesInMilli;
		if (elapsedMinutes != 0) {
			sb.append(elapsedMinutes + "m ");
		}
		dif = dif % minutesInMilli;

		long elapsedSeconds = dif / secondsInMilli;
		if (elapsedSeconds != 0) {
			sb.append(elapsedSeconds + "s");
		}

		// String message = "On the test run at {0,time} on {0,date}, we found {1} prime
		// numbers";
		//
		// MessageFormat mf = new MessageFormat(message);
		//
		// return MessageFormat.format("{0} days, {1} hours {2} minutes {3}", arguments)
		// return mf.format(();

		// return System.out.printf("%d days, %d hours, %d minutes, %d seconds%n",
		// elapsedDays, elapsedHours,
		// elapsedMinutes, elapsedSeconds);

		return sb.toString();
	}

	public static String addStringToBigDecimal(BigDecimal profit, String symbol) {
		String coin = symbol.substring(0, symbol.length() - 3);
		return profit.toString() + " " + coin;
	}

	public static String addBaseCurrencyProfit(BigDecimal profit, BaseCurrency baseCurrency) {
		return profit.toString() + " " + baseCurrency.toString();
	}

}
