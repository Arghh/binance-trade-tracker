package arghh.tradetracker.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import arghh.tradetracker.model.Trade;

public interface TradeRepository extends CrudRepository<Trade, Long> {

	Trade findByBinanceId(Long id);

	List<Trade> findBySymbol(String symbol);

	@Query("select DISTINCT(t.symbol) from Trade t")
	List<String> findDistinctSymbols();

}
