package profittracker.repositories;

import org.springframework.data.repository.CrudRepository;

import profittracker.domain.Trade;

/**
 * Created by jt on 1/10/17.
 */
public interface TradeRepository extends CrudRepository<Trade, Long> {

	Trade findByBinanceId(Long id);

	// @Query("SELECT MAX(t) FROM Trade t")
	// Trade findLastInterId();
}
