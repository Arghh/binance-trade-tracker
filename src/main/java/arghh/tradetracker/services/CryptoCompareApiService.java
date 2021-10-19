package arghh.tradetracker.services;

import java.util.Date;

public interface CryptoCompareApiService {

    String getCurrentValueInFiat(String symbolAndValue);

    String getHistoricalDailyValue(String symbolAndValue, Date date);
}
