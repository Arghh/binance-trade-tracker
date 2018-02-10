package profittracker.commands;

import java.math.BigDecimal;
import java.util.Date;

public class ExcelTrade {
	private Date timeOfTrade;
	private String symbol;
	private boolean buy;
	private BigDecimal price;
	private BigDecimal quantity;
	private BigDecimal total;
	private BigDecimal fee;
	private String symbolOfFee;

	public Date getTimeOfTrade() {
		return timeOfTrade;
	}

	public void setTimeOfTrade(Date timeOfTrade) {
		this.timeOfTrade = timeOfTrade;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public boolean isBuy() {
		return buy;
	}

	public void setBuy(boolean buy) {
		this.buy = buy;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	public String getSymbolOfFee() {
		return symbolOfFee;
	}

	public void setSymbolOfFee(String symbolOfFee) {
		this.symbolOfFee = symbolOfFee;
	}

}
