package arghh.tradetracker.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import arghh.tradetracker.converters.TradeToAggTrade;
import arghh.tradetracker.model.AggregatedTrade;
import arghh.tradetracker.model.Trade;
import arghh.tradetracker.repositories.AggregatedTradeRepository;
import arghh.tradetracker.repositories.TradeRepository;

@Service
public class AggregatedTradeServiceImpl implements AggregatedTradeService {

	private AggregatedTradeRepository aggTradeRepository;
	private TradeRepository tradeRepository;
	private TradeToAggTrade aggTradeConverter;

	@Autowired
	public AggregatedTradeServiceImpl(AggregatedTradeRepository aggTradeRepository, TradeToAggTrade aggTradeConverter,
			TradeRepository tradeRepository) {
		this.aggTradeRepository = aggTradeRepository;
		this.aggTradeConverter = aggTradeConverter;
		this.tradeRepository = tradeRepository;
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
		aggTradeRepository.deleteById(id);
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
