package arghh.tradetracker.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import arghh.tradetracker.model.AggregatedTrade;

@Repository
public interface AggregatedTradeRepository extends CrudRepository<AggregatedTrade, Long> {

	List<AggregatedTrade> findBySymbol(String symbol);

	List<AggregatedTrade> findBySymbolAndProfitIsNull(String symbol);

	@Query("select DISTINCT(at.symbol) from AggregatedTrade at")
	List<String> findDistinctSymbols();

	@Query("select COUNT(at) from AggregatedTrade at")
	Long countAll();

	List<AggregatedTrade> findByTradeTime(Date time);

	AggregatedTrade findByBinanceId(Long Id);

	@Query("select DISTINCT(at.feeCoin) from AggregatedTrade at")
	List<String> findDistinctFeeCoin();

	List<AggregatedTrade> findByFeeCoin(String coin);

	@Query("select at from AggregatedTrade at where profit_fk is null")
	List<AggregatedTrade> findAllWhereProfitIsNull();

}
