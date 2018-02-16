package arghh.tradetracker.commands;

import java.math.BigDecimal;
import java.util.Map;

public class StatsList {

	private String tradeCount;
	private Map<String, Long> marketTrades;
	private Map<String, BigDecimal> marketProfits;

	public String getTradeCount() {
		return tradeCount;
	}

	public void setTradesCount(String tradeCount) {
		this.tradeCount = tradeCount;
	}

	public Map<String, Long> getMarketTrades() {
		return marketTrades;
	}

	public void setMarketTrades(Map<String, Long> marketTrades) {
		this.marketTrades = marketTrades;
	}

	public Map<String, BigDecimal> getMarketProfits() {
		return marketProfits;
	}

	public void setMarketProfits(Map<String, BigDecimal> marketProfits) {
		this.marketProfits = marketProfits;
	}

}
