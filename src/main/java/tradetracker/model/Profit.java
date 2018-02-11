package tradetracker.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import tradetracker.services.BaseCurrency;

@Entity
@Table(name = "profit")
public class Profit {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "base_currency")
	private BaseCurrency baseCurrency;

	@Column(name = "profitValue", precision = 13, scale = 8)
	private BigDecimal profitValue;

	@Column(name = "quantity", precision = 13, scale = 8)
	private BigDecimal quantity;

	@Column(name = "priceDifference", precision = 13, scale = 8)
	private BigDecimal priceDifference;

	private long timeDifference;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "profit")
	private List<AggregatedTrade> aggregatedTrade = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public BigDecimal getPriceDifference() {
		return priceDifference;
	}

	public void setPriceDifference(BigDecimal priceDifference) {
		this.priceDifference = priceDifference;
	}

	public List<AggregatedTrade> getbuySellPair() {
		if (aggregatedTrade == null) {
			return new ArrayList<>();
		}
		return aggregatedTrade;
	}

	public void setbuySellPair(List<AggregatedTrade> buySell) {
		this.aggregatedTrade = buySell;
	}

	public BaseCurrency getBaseCurrency() {
		return baseCurrency;
	}

	public void setBaseCurrency(BaseCurrency baseCurrency) {
		this.baseCurrency = baseCurrency;
	}

	public long getTimeDifference() {
		return timeDifference;
	}

	public void setTimeDifference(long timeDifference) {
		this.timeDifference = timeDifference;
	}

}
