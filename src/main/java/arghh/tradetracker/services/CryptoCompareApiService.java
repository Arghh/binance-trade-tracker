package arghh.tradetracker.services;

import java.math.BigDecimal;

public interface CryptoCompareApiService {

	public BigDecimal getCurrentValueInUsd(String symbolAndValue);
}
