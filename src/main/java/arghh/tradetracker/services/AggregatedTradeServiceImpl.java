package arghh.tradetracker.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import arghh.tradetracker.converters.TradeToAggTrade;
import arghh.tradetracker.model.AggregatedTrade;
import arghh.tradetracker.model.Trade;
import arghh.tradetracker.repositories.AggregatedTradeRepository;
import arghh.tradetracker.repositories.ProfitRepository;

@Service
public class AggregatedTradeServiceImpl implements AggregatedTradeService {

	private AggregatedTradeRepository aggTradeRepository;
	private ProfitRepository profitRepository;
	private TradeToAggTrade aggTradeConverter;

	@Autowired
	public AggregatedTradeServiceImpl(AggregatedTradeRepository aggTradeRepository, TradeToAggTrade aggTradeConverter,
			ProfitRepository profitRepository) {
		this.aggTradeRepository = aggTradeRepository;
		this.aggTradeConverter = aggTradeConverter;
		this.profitRepository = profitRepository;
	}

	@Override
	@Transactional
	public AggregatedTrade saveOrUpdate(AggregatedTrade trade) {
		aggTradeRepository.save(trade);
		return trade;
	}

	@Override
	public List<AggregatedTrade> listAllAggregated() {
		List<AggregatedTrade> trades = new ArrayList<>();
		aggTradeRepository.findAll().forEach(trades::add);
		return trades;
	}

	@Override
	public void delete(Long id) {
		Optional<AggregatedTrade> tradeToDelete = aggTradeRepository.findById(id);
		if (tradeToDelete.get().getProfit() != null) {
			profitRepository.delete(tradeToDelete.get().getProfit());
		}
		aggTradeRepository.delete(tradeToDelete.get());
	}

	@Override
	public AggregatedTrade getById(Long id) {
		return aggTradeRepository.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void convertAndSaveAllAggTrades(List<Trade> trades) {
		AggregatedTrade savedTrade = null;

		for (Trade trade : trades) {
			savedTrade = aggTradeConverter.convert(trade);
			if (savedTrade != null) {
				saveOrUpdate(savedTrade);
				System.out.println("Saved an aggregated trade with ID: " + savedTrade.getId());
			} else {
				System.out.println("Could not convert and save an aggregated trade");
			}
		}

	}

	@Override
	public void deleteAll() {
		aggTradeRepository.deleteAll();
	}

}
