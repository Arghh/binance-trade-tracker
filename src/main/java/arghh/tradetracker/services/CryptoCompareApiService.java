package arghh.tradetracker.services;

import java.math.BigDecimal;
import java.util.Date;

public interface CryptoCompareApiService {

	public BigDecimal getCurrentValueInUsd(String symbolAndValue);

	public BigDecimal getHistoricalDailyValue(String symbolAndValue, Date date);
}
