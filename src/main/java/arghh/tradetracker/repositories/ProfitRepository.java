package arghh.tradetracker.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import arghh.tradetracker.model.Profit;
import arghh.tradetracker.services.BaseCurrency;

@Repository
public interface ProfitRepository extends CrudRepository<Profit, Long> {

    List<Profit> findByBaseCurrency(BaseCurrency currency);

    List<Profit> findBySellTimeBetween(Date start, Date end);

    @Query("select count(p) from Profit p where profit_value > 0")
    Integer findByPositiveProfitValue();

    @Query("select count(p) from Profit p where profit_value < 0")
    Integer findByNegativeProfitValue();

}
