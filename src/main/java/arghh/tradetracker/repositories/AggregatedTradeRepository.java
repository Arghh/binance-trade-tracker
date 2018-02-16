package arghh.tradetracker.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import arghh.tradetracker.model.AggregatedTrade;

public interface AggregatedTradeRepository extends CrudRepository<AggregatedTrade, Long> {
	// @Query("SELECT MAX(t) FROM Trade t")
	// Trade findLastInterId();

	List<AggregatedTrade> findBySymbol(String symbol);

	@Query("select DISTINCT(at.symbol) from AggregatedTrade at")
	List<String> findDistinctSymbols();

	@Query("select COUNT(at) from AggregatedTrade at")
	Long countAll();

	List<AggregatedTrade> findByTradeTime(Date time);

}
