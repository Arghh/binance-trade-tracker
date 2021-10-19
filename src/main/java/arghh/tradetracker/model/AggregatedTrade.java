package arghh.tradetracker.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "aggregated_trade")
public class AggregatedTrade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;
    private String symbol;
    private boolean buy;
    private Long binanceId;
    @Column(name = "price", precision = 13, scale = 9)
    private BigDecimal price;
    @Column(name = "quantity", precision = 13, scale = 9)
    private BigDecimal quantity;
    @Column(name = "fee", precision = 13, scale = 9)
    private BigDecimal fee;
    private String feeCoin;
    @Column(name = "total", precision = 13, scale = 9)
    private BigDecimal total;
    private Date tradeTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profit_fk")
    Profit profit;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "aggregatedTrade", orphanRemoval = true)
    List<Trade> trade = new ArrayList<>();

    public Profit getProfit() {
	return profit;
    }

    public void setProfit(Profit profit) {
	this.profit = profit;
    }

    public BigDecimal getTotal() {
	return total;
    }

    public void setTotal(BigDecimal total) {
	this.total = total;
    }

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
	feeCoin = assetOfFee;
    }

    public Date getTradeTime() {
	return tradeTime;
    }

    public void setTradeTime(Date tradeTime) {
	this.tradeTime = tradeTime;
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

    public List<Trade> getTrade() {
	if (trade == null) {
	    return new ArrayList<>();
	}
	return trade;
    }

    public void setTrade(List<Trade> trade) {
	this.trade = trade;
    }

}
