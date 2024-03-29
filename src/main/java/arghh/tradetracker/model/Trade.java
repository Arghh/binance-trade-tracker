package arghh.tradetracker.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "trade")
@NamedQuery(name = "Trade.findDuplicates", query = "select t from Trade t WHERE t.tradeTime = ?1 "
	+ "and t.symbol = ?2 and t.buy = ?3 and t.price = ?4")
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;
    private String symbol;
    private boolean buy;
    private BigDecimal price;
    private BigDecimal quantity;
    private BigDecimal fee;
    private String feeCoin;
    private BigDecimal total;
    private Date tradeTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aggregated_trade_fk")
    private AggregatedTrade aggregatedTrade;

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

    public BigDecimal getTotal() {
	return total;
    }

    public void setTotal(BigDecimal total) {
	this.total = total;
    }

    public AggregatedTrade getAggregatedTrade() {
	return aggregatedTrade;
    }

    public void setAggregatedTrade(AggregatedTrade aggregatedTrade) {
	this.aggregatedTrade = aggregatedTrade;
    }
}
