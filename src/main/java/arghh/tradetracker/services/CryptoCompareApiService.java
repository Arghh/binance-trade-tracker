package arghh.tradetracker.services;

import java.util.Date;

public interface CryptoCompareApiService {

	public String getCurrentValueInFiat(String symbolAndValue);

	public String getHistoricalDailyValue(String symbolAndValue, Date date);
}
