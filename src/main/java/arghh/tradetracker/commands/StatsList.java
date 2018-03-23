package arghh.tradetracker.commands;

import java.math.BigDecimal;
import java.util.Map;

public class StatsList {

	private String tradeCount;
	private String profitsGained;
	private String profitsLost;
	private Map<String, Long> marketTrades;
	private Map<String, BigDecimal> marketProfits;
	private Map<String, BigDecimal> coinFees;

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

	public Map<String, BigDecimal> getCoinFees() {
		return coinFees;
	}

	public void setCoinFees(Map<String, BigDecimal> coinFees) {
		this.coinFees = coinFees;
	}

	public String getProfitsGained() {
		return profitsGained;
	}

	public void setProfitsGained(String profitsGained) {
		this.profitsGained = profitsGained;
	}

	public String getProfitsLost() {
		return profitsLost;
	}

	public void setProfitsLost(String profitsLost) {
		this.profitsLost = profitsLost;
	}

}
