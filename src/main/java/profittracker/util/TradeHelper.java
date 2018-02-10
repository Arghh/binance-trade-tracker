package profittracker.util;

import java.math.BigDecimal;
import java.util.List;

public class TradeHelper {

	public static BigDecimal addBigDecimals(List<BigDecimal> decimals) {
		return decimals.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	public static BigDecimal substractBigDecimals(BigDecimal buy, BigDecimal sell) {
		return sell.subtract(buy);
	}
}
