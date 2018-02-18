package arghh.tradetracker.services;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import arghh.tradetracker.commands.StatsList;
import arghh.tradetracker.model.AggregatedTrade;
import arghh.tradetracker.repositories.AggregatedTradeRepository;
import arghh.tradetracker.util.TradeHelper;

@Service
public class StatsServiceImpl implements StatsService {

	private AggregatedTradeRepository aggTradeRepository;

	@Autowired
	public StatsServiceImpl(AggregatedTradeRepository aggTradeRepository) {
		this.aggTradeRepository = aggTradeRepository;
	}

	@Override
	public StatsList showAllStats() {
		StatsList statsForWeb = new StatsList();

		// front end needs a string
		statsForWeb.setTradesCount(aggTradeRepository.countAll().toString());

		List<String> allSymbols = aggTradeRepository.findDistinctSymbols();
		List<String> allFeeCoins = aggTradeRepository.findDistinctFeeCoin();

		statsForWeb.setMarketTrades(findTradeCountProMarket(allSymbols));
		statsForWeb.setMarketProfits(findProfitsProMarket(allSymbols));
		statsForWeb.setCoinFees(findTotalFees(allFeeCoins));

		return statsForWeb;
	}

	private Map<String, Long> findTradeCountProMarket(List<String> allTrades) {
		Map<String, Long> tradesProMarket = new HashMap<>();
		for (String s : allTrades) {
			Long tradeCount = aggTradeRepository.findBySymbol(s).stream().count();
			tradesProMarket.put(s, tradeCount);
		}

		Map<String, Long> sortedTradesProMarket = sortMapBiggestValueFirstLong(tradesProMarket);

		return sortedTradesProMarket;

	}

	private Map<String, BigDecimal> findProfitsProMarket(List<String> allTrades) {

		Map<String, BigDecimal> profitsProMarket = new HashMap<>();

		for (String s : allTrades) {
			List<AggregatedTrade> trades = aggTradeRepository.findBySymbol(s);

			if (trades.size() > 1) {
				List<AggregatedTrade> filteredTrades = trades.stream().filter(x -> x.getProfit() != null)
						.collect(Collectors.toList());

				BigDecimal totalProfitProCurrency = TradeHelper.addBigDecimals(filteredTrades.stream()
						.filter(x -> !x.isBuy()).map(x -> x.getProfit().getProfitValue()).collect(Collectors.toList()));
				profitsProMarket.put(s, totalProfitProCurrency);
			}
		}

		Map<String, BigDecimal> sortedProfits = sortMapBiggestValueFirstDecimal(profitsProMarket);

		return sortedProfits;
	}

	private Map<String, BigDecimal> findTotalFees(List<String> allFeeCoins) {
		Map<String, BigDecimal> fees = new HashMap<>();

		for (String s : allFeeCoins) {
			List<AggregatedTrade> trades = aggTradeRepository.findByFeeCoin(s);

			BigDecimal totalFeesProCoin = TradeHelper
					.addBigDecimals(trades.stream().map(x -> x.getFee()).collect(Collectors.toList()));

			fees.put(s, totalFeesProCoin);
		}

		Map<String, BigDecimal> sortedFees = sortMapBiggestValueFirstDecimal(fees);

		return sortedFees;
	}

	private Map<String, Long> sortMapBiggestValueFirstLong(Map<String, Long> unsorted) {
		return unsorted.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	}

	private LinkedHashMap<String, BigDecimal> sortMapBiggestValueFirstDecimal(Map<String, BigDecimal> unsorted) {
		return unsorted.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue,
						LinkedHashMap::new));
	}
}
