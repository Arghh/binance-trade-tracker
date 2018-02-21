package arghh.tradetracker.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import arghh.tradetracker.model.Profit;
import arghh.tradetracker.services.BaseCurrency;

@Repository
public interface ProfitRepository extends CrudRepository<Profit, Long> {

	List<Profit> findByBaseCurrency(BaseCurrency currency);

	List<Profit> findBySellTimeBetween(Date start, Date end);

}
