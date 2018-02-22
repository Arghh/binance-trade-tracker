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
import arghh.tradetracker.repositories.TradeRepository;

@Service
public class AggregatedTradeServiceImpl implements AggregatedTradeService {

	private AggregatedTradeRepository aggTradeRepository;
	private ProfitRepository profitRepository;
	private TradeToAggTrade aggTradeConverter;
	private TradeRepository tradeRepositorty;

	@Autowired
	public AggregatedTradeServiceImpl(AggregatedTradeRepository aggTradeRepository, TradeToAggTrade aggTradeConverter,
			ProfitRepository profitRepository, TradeRepository tradeRepositorty) {
		this.aggTradeRepository = aggTradeRepository;
		this.aggTradeConverter = aggTradeConverter;
		this.profitRepository = profitRepository;
		this.tradeRepositorty = tradeRepositorty;
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
		aggTradeRepository.delete(tradeToDelete.get());
	}

	@Override
	public AggregatedTrade getById(Long id) {
		return aggTradeRepository.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void saveAllAggTrades(List<AggregatedTrade> trades) {

		for (AggregatedTrade trade : trades) {
			saveOrUpdate(trade);
			System.out.println("Saved an aggregated trade with ID: " + trade.getId());

			if (!trade.getTrade().isEmpty()) {
				for (Trade tradeToUpdate : trade.getTrade()) {
					tradeToUpdate.setAggregatedTrade(trade);
					tradeRepositorty.save(tradeToUpdate);
					System.out.println("Updated the raw trade with the ID:" + tradeToUpdate.getId());
				}
			}
		}

	}

	@Override
	public void deleteAll() {
		aggTradeRepository.deleteAll();
		System.out.println("Deleted all trades");
	}

	@Override
	public List<AggregatedTrade> listAllUnmatchedTrades() {
		List<AggregatedTrade> unMatchedTrades = aggTradeRepository.findAllWhereProfitIsNull();
		return unMatchedTrades;
	}

}
