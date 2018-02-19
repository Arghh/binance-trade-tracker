package arghh.tradetracker.services;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
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

		System.out.println("Starting task: Calculate profits");
		// TODO: use stream groupingBy to divide trades into groups
		// Map<Enum, Set<AggregatedTrade>> tradesBySymbols = trades.stream()
		// .collect(groupingBy(AggregatedTrade::getSymbol, toSet()));
		// sort and filter trades into groups
		for (String s : symbols) {

			trades = aggTradeRepository.findBySymbolAndProfitIsNull(s);

			System.out.println("Calculating profits for trade pair " + s);
			if (!trades.isEmpty() || !(trades.size() < 2)) {
				trades = filterOutOpenTrades(trades);
			}

			// skip profits with no trade pair
			if (trades.isEmpty() || trades.size() < 2) {
				System.out.println(
						MessageFormat.format("The trade pair {0} only has {1} new trades. Skipping", s, trades.size()));
			} else {
				Collection<List<AggregatedTrade>> buySellSet = createTradePairs(trades);
				buySellSet.forEach(x -> saveNewProfit(x));
			}

			System.out.println("Finished calculating profits for trade pair " + s);
		}
		System.out.println("Finished task: Calculate profits");

	}

	private List<AggregatedTrade> filterOutOpenTrades(List<AggregatedTrade> allTrades) {
		List<AggregatedTrade> filteredTrades = allTrades;

		if (filteredTrades.get(filteredTrades.size() - 1).isBuy()) {
			filteredTrades.remove(filteredTrades.size() - 1);
			System.out.println("Skipping last trade because it's still open");
		}

		// filter out open trades if the quantity has not been sold TODO: unless the fee
		// coin
		// is NOT BNB
		for (int i = 0; i < filteredTrades.size() - 1; i++) {
			if (filteredTrades.get(i).isBuy()) {
				// if the next trade is also a buy
				if (filteredTrades.get(i + 1).isBuy()) {
					// if the next trade after a buy is sell with same quantity skip
					// otherwise remove buys
					filteredTrades.remove(i);

				}
				System.out.println("Skipping a trade because it's still open");
			}
		}

		// // filter out sells that happend later
		for (int i = 0; i < filteredTrades.size(); i++) {
			if (filteredTrades.get(i).isBuy() && !filteredTrades.get(i + 1).isBuy()) {
				// with BNB the quantity will be different
				if (!filteredTrades.get(i).getFeeCoin().equals("BNB")) {
					continue;
				} else if (filteredTrades.get(i).getQuantity()
						.compareTo(filteredTrades.get(i + 1).getQuantity()) != 0) {
					filteredTrades.remove(i + 1);
				}
			}
		}

		// TODO: same quantity buy order after eachother. later find the matching sell

		return filteredTrades;

	}

	private Collection<List<AggregatedTrade>> createTradePairs(List<AggregatedTrade> filteredTrades) {
		// divide the even list into sets each size 2
		final AtomicInteger counter = new AtomicInteger(0);

		final Collection<List<AggregatedTrade>> pairs = filteredTrades.stream()
				.collect(Collectors.groupingBy(t -> counter.getAndIncrement() / 2)).values();
		//
		// Map<Object, List<AggregatedTrade>> pairs =
		// filteredTrades.stream().collect(Collectors.groupingBy(s -> s / 2));

		return pairs;

	}

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
	public List<String> allTimeProfits() {
		List<String> allProfits = new ArrayList<>();
		for (BaseCurrency currency : BaseCurrency.values()) {
			List<Profit> profits = profitRepository.findByBaseCurrency(currency);

			if (!profits.isEmpty()) {
				allProfits.add(calculateProfitProCurrency(profits, currency));
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
		System.out.println("Deleted all profits");
	}

	@Override
	public void delete(Long id) {
		profitRepository.deleteById(id);
	}

	@Override
	public List<ProfitList> listDailyTradeProfits(String date) {
		List<ProfitList> profitsForWeb = new ArrayList<>();
		if (date != null) {
			Date day = stringToDate(date);

			List<Profit> profits = profitRepository.findBySellTimeBetween(TradeHelper.getStartOfDay(day),
					TradeHelper.getEndOfDay(day));
			profitsForWeb = webConverter.convert(profits);
		}

		return profitsForWeb;
	}

	private Date stringToDate(String day) {
		TimeZone tz = TimeZone.getTimeZone("Europe/London");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		formatter.setTimeZone(tz);
		try {
			return formatter.parse(day);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	@Override
	public List<String> calculatedTotalDailyProfits(String date) {
		List<String> dailyTotalProfits = new ArrayList<>();
		Date day = stringToDate(date);
		List<Profit> profits = profitRepository.findBySellTimeBetween(TradeHelper.getStartOfDay(day),
				TradeHelper.getEndOfDay(day));

		for (BaseCurrency currency : BaseCurrency.values()) {
			List<Profit> profitsProCurrency = profits.stream().filter(x -> x.getBaseCurrency() == currency)
					.collect(Collectors.toList());

			if (!profitsProCurrency.isEmpty()) {
				dailyTotalProfits.add(calculateProfitProCurrency(profits, currency));
			}
		}

		return dailyTotalProfits;
	}

	private String calculateProfitProCurrency(List<Profit> profits, BaseCurrency currency) {
		BigDecimal totalProfitProCurrency = TradeHelper
				.addBigDecimals(profits.stream().map(x -> x.getProfitValue()).collect(Collectors.toList()));

		return TradeHelper.addBaseCurrencyProfit(totalProfitProCurrency, currency);
	}

}
