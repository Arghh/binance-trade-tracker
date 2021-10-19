package arghh.tradetracker.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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

	try {
	    return BaseCurrency.valueOf(currency);
	} catch (IllegalArgumentException e) {
	    System.out.println("The market symbol " + symbol + " is invalid");
	}
	return null;

    }

    public static long getMsBetweenTrades(Date buy, Date sell) throws ProfitException {
	TimeZone tz = TimeZone.getTimeZone("UTC");
	SimpleDateFormat formatter = new SimpleDateFormat("dd HH:mm:ss");
	formatter.setTimeZone(tz);

	String buyTime = formatter.format(buy.getTime());
	String sellTime = formatter.format(sell.getTime());

	if (buy.after(sell)) {
	    throw new ProfitException("Buy time " + buyTime + " can't come after sell time " + sellTime);
	}

	return sell.getTime() - buy.getTime();

    }

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
	    sb.append(elapsedDays + " d, ");
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

	return sb.toString();
    }

    public static String addStringToBigDecimal(BigDecimal profit, String symbol) {
	String coin = symbol.substring(0, symbol.length() - 3);
	String profitForWeb = removeExtraZerosForWebTables(profit);
	return profitForWeb + " " + coin;
    }

    public static String addBaseCurrencyProfit(BigDecimal profit, BaseCurrency baseCurrency) {
	return profit.toPlainString() + " " + baseCurrency.toString();
    }

    private static String removeExtraZerosForWebTables(BigDecimal bd) {
	String WithZeros = bd.toPlainString();
	return WithZeros.indexOf(".") < 0 ? WithZeros : WithZeros.replaceAll("0*$", "").replaceAll("\\.$", "");
    }

    // TODO: Use java.time instead of java.util.date
    public static Date getEndOfDay(Date date) {
	Calendar calendar = Calendar.getInstance();
	calendar.setTime(date);
	calendar.set(Calendar.HOUR_OF_DAY, 23);
	calendar.set(Calendar.MINUTE, 59);
	calendar.set(Calendar.SECOND, 59);
	calendar.set(Calendar.MILLISECOND, 999);
	return calendar.getTime();
    }

    public static Date getStartOfDay(Date date) {
	Calendar calendar = Calendar.getInstance();
	calendar.setTime(date);
	calendar.set(Calendar.HOUR_OF_DAY, 0);
	calendar.set(Calendar.MINUTE, 0);
	calendar.set(Calendar.SECOND, 0);
	calendar.set(Calendar.MILLISECOND, 0);
	return calendar.getTime();
    }

}
