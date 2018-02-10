package profittracker.domain;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "profit")
public class Profit {

	@Id
	@GeneratedValue
	private Long id;
	private String baseSymbol;
	@Column(name = "profitValue", precision = 13, scale = 8)
	private BigDecimal profitValue;
	@Column(name = "quantity", precision = 13, scale = 8)
	private BigDecimal quantity;
	@Column(name = "priceDifferents", precision = 13, scale = 8)
	private BigDecimal priceDifferents;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "profit")
	private Set<AggregatedTrade> aggregatedTrade = new HashSet<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBaseSymbol() {
		return baseSymbol;
	}

	public void setBaseSymbol(String baseSymbol) {
		this.baseSymbol = baseSymbol;
	}

	public BigDecimal getProfitValue() {
		return profitValue;
	}

	public void setProfitValue(BigDecimal profitValue) {
		this.profitValue = profitValue;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getPriceDifferents() {
		return priceDifferents;
	}

	public void setPriceDifferents(BigDecimal priceDifferents) {
		this.priceDifferents = priceDifferents;
	}

	public Set<AggregatedTrade> getbuySellPair() {
		if (aggregatedTrade == null) {
			return new HashSet<>();
		}
		return aggregatedTrade;
	}

	public void setbuySellPair(Set<AggregatedTrade> buySell) {
		this.aggregatedTrade = buySell;
	}

}
