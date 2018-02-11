package tradetracker.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tradetracker.commands.ProfitList;
import tradetracker.converters.AggTradeToProfit;
import tradetracker.converters.ProfitToProfitList;
import tradetracker.model.AggregatedTrade;
import tradetracker.model.Profit;
import tradetracker.repositories.AggregatedTradeRepository;
import tradetracker.repositories.ProfitRepository;
import tradetracker.util.TradeHelper;

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
			System.out.println("Calculating profits for trade pair " + trades.get(0).getSymbol());
			trades = filterOutOpenTrades(trades);
			Collection<List<AggregatedTrade>> buySellSet = createTradePairs(trades);
			buySellSet.forEach(x -> saveNewProfit(x));

			// for (Set<AggregatedTrade> set : buySellSet) {
			// set.forEach(x -> {
			// System.out.println(x.getId());
			// System.out.println(x.isBuy());
			// });
			// }
			System.out.println("Finished calculating profits for trade pair " + trades.get(0).getSymbol());
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
		if (!(filteredTrades.size() % 2 == 0)) {
			filteredTrades.remove(filteredTrades.size() - 1);
			System.out.println("Skipping last trade because it's still open");
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
			System.out.println("Saved Trade with ID: " + savedProfit.getId());
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
	public BigDecimal totalProfits() {
		List<Profit> profits = listAllProfits();
		List<BigDecimal> decimals = profits.stream().map(x -> x.getPriceDifference()).collect(Collectors.toList());

		return TradeHelper.addBigDecimals(decimals);

	}

	@Override
	public List<ProfitList> showAllProfits() {
		List<ProfitList> profitsForWeb = webConverter.convert(listAllProfits());
		return profitsForWeb;
	}

}
