package profittracker.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "trade")
public class Trade {

	@Id
	@GeneratedValue
	private Long id;
	private String symbol;
	private Long binanceId;
	private boolean buy;
	@Column(name = "price", precision = 13, scale = 9)
	private BigDecimal price;
	@Column(name = "quantity", precision = 13, scale = 9)
	private BigDecimal quantity;
	@Column(name = "fee", precision = 13, scale = 9)
	private BigDecimal fee;
	private String feeCoin;
	@Column(name = "total", precision = 13, scale = 9)
	private BigDecimal total;

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	private Date timeOfTrade;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	public String getFeeCoin() {
		return feeCoin;
	}

	public void setFeeCoin(String assetOfFee) {
		this.feeCoin = assetOfFee;
	}

	public Date getTimeOfTrade() {
		return timeOfTrade;
	}

	public void setTimeOfTrade(Date timeOfTrade) {
		this.timeOfTrade = timeOfTrade;
	}

	public boolean isBuy() {
		return buy;
	}

	public void setBuy(boolean buy) {
		this.buy = buy;
	}

	public Long getBinanceId() {
		return binanceId;
	}

	public void setBinanceId(Long binanceId) {
		this.binanceId = binanceId;
	}
}
