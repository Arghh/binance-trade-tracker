package tradetracker.repositories;

import org.springframework.data.repository.CrudRepository;

import tradetracker.model.Profit;

public interface ProfitRepository extends CrudRepository<Profit, Long> {

}
