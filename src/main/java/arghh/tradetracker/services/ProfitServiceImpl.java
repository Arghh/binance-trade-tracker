package arghh.tradetracker.services;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import arghh.tradetracker.commands.ProfitList;
import arghh.tradetracker.converters.AggTradeToProfit;
import arghh.tradetracker.converters.ProfitToProfitList;
import arghh.tradetracker.model.AggregatedTrade;
import arghh.tradetracker.model.Profit;
import arghh.tradetracker.repositories.AggregatedTradeRepository;
import arghh.tradetracker.repositories.ProfitRepository;
import arghh.tradetracker.util.TradeHelper;

@Service
public class ProfitServiceImpl implements ProfitService {

	private AggregatedTradeRepository aggTradeRepository;
	private ProfitRepository profitRepository;
	private AggregatedTradeService aggTradeService;
	private AggTradeToProfit profitConverter;
	private ProfitToProfitList webConverter;

	@Autowired
	public ProfitServiceImpl(AggregatedTradeRepository aggTradeRepository, AggregatedTradeService aggTradeService,
			ProfitRepository profitRepository, AggTradeToProfit profitConverter, ProfitToProfitList webConverter) {
		this.aggTradeRepository = aggTradeRepository;
		this.aggTradeService = aggTradeService;
		this.profitRepository = profitRepository;
		this.profitConverter = profitConverter;
		this.webConverter = webConverter;
	}

	@Override
	public List<Profit> listAllProfits() {
		List<Profit> profit = new ArrayList<>();
		profitRepository.findAll().forEach(profit::add);
		return profit;
	}

	@Override
	public Profit saveOrUpdate(Profit profit) {
		profitRepository.save(profit);
		return profit;
	}

	@Override
	public void saveAllTradeProfits() {
		List<AggregatedTrade> trades = aggTradeService.listAllAggregated();
		List<String> symbols = aggTradeRepository.findDistinctSymbols();

		// TODO: use stream groupingBy to divide trades into groups
		// Map<Enum, Set<AggregatedTrade>> tradesBySymbols = trades.stream()
		// .collect(groupingBy(AggregatedTrade::getSymbol, toSet()));
		// sort and filter trades into groups
		for (String s : symbols) {

			trades = aggTradeRepository.findBySymbol(s);
			System.out.println("Calculating profits for trade pair " + s);
			trades = filterOutOpenTrades(trades);

			// skip profits with no trade pair
			if (trades.isEmpty() || trades.size() < 2) {
				System.out.println(MessageFormat.format("The trade pair {0} only has {1} trades saved. Skipping", s,
						trades.size()));
			} else {
				Collection<List<AggregatedTrade>> buySellSet = createTradePairs(trades);
				buySellSet.forEach(x -> saveNewProfit(x));
			}

			// for (Set<AggregatedTrade> set : buySellSet) {
			// set.forEach(x -> {
			// System.out.println(x.getId());
			// System.out.println(x.isBuy());
			// });
			// }
			System.out.println("Finished calculating profits for trade pair " + s);
		}

		// convert and save all trade profits
		// for (AggregatedTrade trade : trades) {
		// saveNewProfit(trade);
		// }
	}

	private List<AggregatedTrade> filterOutOpenTrades(List<AggregatedTrade> allTrades) {
		List<AggregatedTrade> filteredTrades = allTrades;
		if (!filteredTrades.get(0).isBuy()) {
			filteredTrades.remove(0);
			System.out.println("Skipping a trade. First trade has to be a buy");
		}
		if (filteredTrades.get(filteredTrades.size() - 1).isBuy()) {
			filteredTrades.remove(filteredTrades.size() - 1);
			System.out.println("Skipping last trade because it's still open");
		}
		// if (!(filteredTrades.size() % 2 == 0)) {
		// filteredTrades.remove(filteredTrades.size() - 1);
		// System.out.println("Skipping last trade because it's still open");
		// }
		// filter out open trades if the quantity has not been sold
		for (int i = 0; i < filteredTrades.size() - 2; i++) {
			if (filteredTrades.get(i).isBuy()) {
				// if the next trade is also a buy
				while (filteredTrades.get(i + 1).isBuy()) {
					// if the next trade after a buy is sell with same quantity skip
					if (!filteredTrades.get(i + 2).isBuy() && filteredTrades.get(i).getQuantity()
							.compareTo(filteredTrades.get(i + 1).getQuantity()) == 0)
						continue;

					// otherwise remove buys
					filteredTrades.remove(i);
					System.out.println("Skipping a trade because it's still open");
				}
			}
		}
		return filteredTrades;
	}

	private Collection<List<AggregatedTrade>> createTradePairs(List<AggregatedTrade> filteredTrades) {
		// divide the even list into sets each size 2
		final AtomicInteger counter = new AtomicInteger(0);

		// Integer key = entry.getKey();
		// String value = entry.getValue();
		// Profit profit = new Profit();
		final Collection<List<AggregatedTrade>> pairs = filteredTrades.stream()
				.collect(Collectors.groupingBy(t -> counter.getAndIncrement() / 2)).values();
		//
		// Map<Object, List<AggregatedTrade>> pairs =
		// filteredTrades.stream().collect(Collectors.groupingBy(s -> s / 2));

		return pairs;

	}
	// private List<AggregatedTrade> calculateProfitsPerSymbol(List<AggregatedTrade>
	// allTrades) {
	// BigDecimal buyTotal = allTrades.get(i).getTotal();
	// BigDecimal sellTotal = allTrades.get(i + 1).getTotal();
	// BigDecimal profit = TradeHelper.substractBigDecimals(buyTotal, sellTotal);
	//
	// }

	private Profit saveNewProfit(List<AggregatedTrade> buySellPair) {
		Profit savedProfit = profitConverter.convert(buySellPair);
		if (savedProfit != null) {
			saveOrUpdate(savedProfit);
			System.out.println(MessageFormat.format("Saved a profit with ID: {0} and currency {1}", savedProfit.getId(),
					savedProfit.getBaseCurrency()));
		} else {
			System.out.println("Could not convert and save a profit");
		}

		return savedProfit;
	}

	@Override
	public List<Profit> listDailyTradeProfits() {
		// TODO Auto-generated method stub
		return new ArrayList<>();
	}

	@Override
	public List<String> totalProfits() {
		List<String> allProfits = new ArrayList<>();
		for (BaseCurrency currency : BaseCurrency.values()) {
			List<Profit> profits = profitRepository.findByBaseCurrency(currency);

			if (!profits.isEmpty()) {
				BigDecimal totalProfitProCurrency = TradeHelper
						.addBigDecimals(profits.stream().map(x -> x.getProfitValue()).collect(Collectors.toList()));
				allProfits.add(TradeHelper.addBaseCurrencyProfit(totalProfitProCurrency, currency));
			}
		}

		return allProfits;

	}

	@Override
	public List<ProfitList> showAllProfits() {
		List<ProfitList> profitsForWeb = webConverter.convert(listAllProfits());
		return profitsForWeb;
	}

	@Override
	public void deleteAll() {
		profitRepository.deleteAll();
	}

}
