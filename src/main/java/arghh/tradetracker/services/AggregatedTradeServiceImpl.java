package arghh.tradetracker.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import arghh.tradetracker.exception.ProfitException;
import arghh.tradetracker.model.AggregatedTrade;
import arghh.tradetracker.model.Trade;
import arghh.tradetracker.repositories.AggregatedTradeRepository;
import arghh.tradetracker.repositories.TradeRepository;
import arghh.tradetracker.util.TradeHelper;

@Service
public class AggregatedTradeServiceImpl implements AggregatedTradeService {

	private AggregatedTradeRepository aggTradeRepository;
	private TradeRepository tradeRepositorty;

	@Autowired
	public AggregatedTradeServiceImpl(AggregatedTradeRepository aggTradeRepository, TradeRepository tradeRepositorty) {
		this.aggTradeRepository = aggTradeRepository;
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

	@Override
	public List<AggregatedTrade> matchTrades(List<Integer> ids) {
		List<AggregatedTrade> trades = new ArrayList<>();
		for (Integer integer : ids) {
			AggregatedTrade trade = getById((long) integer);
			if (trade != null) {
				trades.add(trade);
			} else {
				System.out.println("Could not find an AggregatedTrade with the ID " + integer);
			}
		}

		if (trades.size() < 2) {
			System.out.println("You need to select at least 2 trades to combine into one profit");
			return null;
		}
		if (!trades.stream().anyMatch(x -> x.isBuy())) {
			System.out.println("There needs to be at least one buy order selected");
			return null;
		}
		if (!trades.stream().anyMatch(x -> !x.isBuy())) {
			System.out.println("There needs to be at least one sell order selected");
			return null;
		}
		if (!trades.stream().allMatch(x -> x.getSymbol().equals(trades.get(0).getSymbol()))) {
			System.out.println("The market needs to be same for all the selected trades");
			return null;
		}

		AggregatedTrade buy = trades.stream().filter(x -> x.isBuy()).findFirst().get();
		AggregatedTrade sell = trades.stream().filter(x -> !x.isBuy()).findFirst().get();
		try {
			TradeHelper.getMsBetweenTrades(buy.getTradeTime(), sell.getTradeTime());
		} catch (ProfitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		List<AggregatedTrade> sorted = trades.stream().sorted(Comparator.comparing(AggregatedTrade::isBuy).reversed())
				.collect(Collectors.toList());

		return sorted;
	}

}
