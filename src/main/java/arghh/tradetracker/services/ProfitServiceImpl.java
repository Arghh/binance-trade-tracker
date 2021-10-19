package arghh.tradetracker.services;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import arghh.tradetracker.commands.ProfitList;
import arghh.tradetracker.converters.AggTradeListToProfit;
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
    private CryptoCompareApiService cryptoApi;
    private AggTradeListToProfit profitListConverter;

    @Autowired
    public ProfitServiceImpl(AggregatedTradeRepository aggTradeRepository, AggregatedTradeService aggTradeService,
	    ProfitRepository profitRepository, AggTradeToProfit profitConverter, ProfitToProfitList webConverter,
	    CryptoCompareApiService cryptoApi, AggTradeListToProfit profitListConverter) {

	this.aggTradeRepository = aggTradeRepository;
	this.aggTradeService = aggTradeService;
	this.profitRepository = profitRepository;
	this.profitConverter = profitConverter;
	this.webConverter = webConverter;
	this.cryptoApi = cryptoApi;
	this.profitListConverter = profitListConverter;
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

	// sort and filter trades into groups
	for (String s : symbols) {

	    trades = aggTradeRepository.findBySymbolAndProfitIsNull(s);

	    if (trades.isEmpty() || trades.size() < 2) {
		System.out.println(MessageFormat
			.format("The trade pair {0} does not have enough trades to calculate profits.", s));
	    } else {
		AggregatedTrade buy = trades.stream().filter(AggregatedTrade::isBuy).findFirst().orElse(null);
		AggregatedTrade sell = trades.stream().filter(x -> !x.isBuy()).findFirst().orElse(null);

		if (buy == null || sell == null) {
		    System.out.println(MessageFormat
			    .format("The trade pair {0} has only buy or sell trades. Can't calculate profits", s));
		} else {
		    System.out.println("Calculating profits for trade pair " + s);
		    trades = filterOutOpenTrades(trades);
		    System.out.println("Finished calculating profits for trade pair " + s);
		}
	    }
	}
	System.out.println("Finished task: Calculate profits");

    }

    private List<AggregatedTrade> filterOutOpenTrades(List<AggregatedTrade> allTrades) {
	List<AggregatedTrade> filteredTrades = allTrades;

	while (!filteredTrades.get(0).isBuy()) {
	    filteredTrades.remove(filteredTrades.get(0));
	    System.out.println("First trade has to be a buy. Skipping");
	}

	if (filteredTrades.get(filteredTrades.size() - 1).isBuy()) {
	    filteredTrades.remove(filteredTrades.size() - 1);
	    System.out.println("Skipping last trade because it's still open");
	}

	List<AggregatedTrade> simpleTrades = new ArrayList<>();
	List<AggregatedTrade> unclearTrades = new ArrayList<>();

	if (filteredTrades.size() > 1) {

	    for (int i = 0; i < filteredTrades.size(); i++) {

		// filter out and save easy trade pairs with 0 sum and buy sell
		if (filteredTrades.get(i).isBuy() && !filteredTrades.get(i + 1).isBuy()) {

		    if (filteredTrades.get(i).getQuantity().compareTo(filteredTrades.get(i + 1).getQuantity()) == 0
			    || !filteredTrades.get(i).getFeeCoin().equals(BaseCurrency.BNB.toString())) {
			simpleTrades.add(filteredTrades.get(i));
			simpleTrades.add(filteredTrades.get(i + 1));

			// filter out and save easy buy sells when fee currency is not BNB TODO:
			// calculate profits correctly. now we always lose profit because we sell
			// smaller amout than we bought
		    } else {
			unclearTrades.add(filteredTrades.get(i));
			unclearTrades.add(filteredTrades.get(i + 1));
		    }
		    // skip over the sell because we add pairs together
		    i++;
		} else {
		    unclearTrades.add(filteredTrades.get(i));
		}
	    }
	}

	// skip profits with no trade pair
	if (simpleTrades.isEmpty() || simpleTrades.size() < 2) {
	    System.out.println("Could not find any 1 to 1 buy/sell pairs. Trying to calculate profits.");
	} else {
	    Collection<List<AggregatedTrade>> buySellSet = createTradePairs(simpleTrades);
	    buySellSet.forEach(this::saveNewProfit);
	}

	sortAndSaveUnclearTradesToProfits(unclearTrades);

	return filteredTrades;

    }

    private void sortAndSaveUnclearTradesToProfits(List<AggregatedTrade> unclearTrades) {
	List<AggregatedTrade> tradesToCombine = unclearTrades;
	// weird stuff here like partial amounts bought and sold. add all sales or buys
	// together and try to find a quantity that sums to 0 then
	// save TODO: also withdraws
	if (tradesToCombine.size() > 2) {
	    for (int i = 0; i < tradesToCombine.size() - 1; i++) {
		LinkedList<AggregatedTrade> partialTrades = new LinkedList<>();

		partialTrades.add(tradesToCombine.get(i));
		while (tradesToCombine.get(i).isBuy() == tradesToCombine.get(i + 1).isBuy()) {

		    partialTrades.add(tradesToCombine.get(i + 1));

		    // skip every trade we add to the partialTradesList
		    i++;

		    // just in case!
		    if (i == tradesToCombine.size() - 1) {
			System.out.println("The trade pair " + partialTrades.get(0).getSymbol()
				+ " only has buy or sell trades. Can't calculate profits. Please check");
			break;
		    }
		}

		// if we found something useful
		if (partialTrades.size() > 1) {
		    List<BigDecimal> allQuantities = new ArrayList<>();

		    for (AggregatedTrade at : partialTrades) {
			allQuantities.add(at.getQuantity());
		    }
		    BigDecimal totalAmount = TradeHelper.addBigDecimals(allQuantities);

		    if (!partialTrades.get(0).isBuy()) {
			// if the next trade before the sell list is a buy
			if (totalAmount.compareTo(tradesToCombine.get(i - partialTrades.size()).getQuantity()) == 0) {
			    AggregatedTrade buyTrade = tradesToCombine.get(i - partialTrades.size());
			    partialTrades.add(buyTrade);
			    if (partialTrades.size() > 2) {
				convertAndSaveProfitLists(partialTrades);
			    } else {
				System.out.println("partialTrades list size has to be at least 3.");
			    }
			}

		    } else if (partialTrades.get(0).isBuy()) {
			// if the next trade after the buy list is a sell
			if (totalAmount.compareTo(tradesToCombine.get(partialTrades.size()).getQuantity()) == 0) {
			    AggregatedTrade sellTrade = tradesToCombine.get(partialTrades.size());
			    partialTrades.add(sellTrade);

			    if (partialTrades.size() > 2) {
				convertAndSaveProfitLists(partialTrades);

			    } else {
				System.out.println("partialTrades list size has to be at least 3.");
			    }
			}
		    } else {
			System.out.println("Those sell and buy orders pairs in " + partialTrades.get(0).getSymbol()
				+ " do not make sense. Skipping");
			System.out.println("Delete the following trades:");

			for (AggregatedTrade ats : partialTrades) {
			    System.out.println("Trade time: " + ats.getTradeTime() + " trade quantity: "
				    + ats.getQuantity() + " and price: " + ats.getPrice());
			}
		    }
		}
	    }
	}
    }

    @Override
    @Transactional
    public void convertAndSaveProfitLists(List<AggregatedTrade> partialTrades) {
	Profit profitToSave = profitListConverter.convert(partialTrades);
	if (profitToSave != null) {
	    saveOrUpdate(profitToSave);
	    System.out.println("Saved a profit from combined trades.");
	} else {
	    System.out.println("Something went wrong. Could not convert and save partial profits");
	}
    }

    private Collection<List<AggregatedTrade>> createTradePairs(List<AggregatedTrade> filteredTrades) {

	// divide the even list into sets each size 2
	final AtomicInteger counter = new AtomicInteger(0);

	return filteredTrades.stream().collect(Collectors.groupingBy(t -> counter.getAndIncrement() / 2)).values();

    }

    @Override
    @Transactional
    public Profit saveNewProfit(List<AggregatedTrade> buySellPair) {
	Profit savedProfit = profitConverter.convert(buySellPair);
	if (savedProfit != null) {
	    saveOrUpdate(savedProfit);
	    System.out.println(MessageFormat.format(
		    "Saved a profit with ID: {0}, currency {1}, quantity {2} and profit {3}", savedProfit.getId(),
		    savedProfit.getBaseCurrency(), savedProfit.getQuantity().stripTrailingZeros().toPlainString(),
		    savedProfit.getProfitValue().stripTrailingZeros().toPlainString()));
	} else {
	    System.out.println("Something went wrong. Could not convert and save a profit pair");
	}

	return savedProfit;
    }

    @Override
    public List<String> allTimeProfitsSumInCurrencies() {
	List<String> allProfits = new ArrayList<>();
	for (BaseCurrency currency : BaseCurrency.values()) {
	    List<Profit> profits = profitRepository.findByBaseCurrency(currency);

	    if (!profits.isEmpty()) {
		String profitAndCurrency = calculateProfitProCurrency(profits, currency);
		String valueInFiat = cryptoApi.getCurrentValueInFiat(profitAndCurrency);
		if (valueInFiat != null) {
		    profitAndCurrency = profitAndCurrency + " ( " + valueInFiat + " )";
		}

		allProfits.add(profitAndCurrency);
	    }
	}

	return allProfits;
    }

    @Override
    public List<ProfitList> showAllProfits() {
	return webConverter.convert(listAllProfits());
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
	TimeZone tz = TimeZone.getTimeZone("UTC");
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
		String profitAndCurrency = calculateProfitProCurrency(profitsProCurrency, currency);
		String valueInFiat = cryptoApi.getHistoricalDailyValue(profitAndCurrency, day);

		if (valueInFiat != null) {
		    profitAndCurrency = profitAndCurrency + " ( " + valueInFiat + " )";
		}

		dailyTotalProfits.add(profitAndCurrency);
	    }
	}

	return dailyTotalProfits;
    }

    private String calculateProfitProCurrency(List<Profit> profits, BaseCurrency currency) {
	BigDecimal totalProfitProCurrency = TradeHelper
		.addBigDecimals(profits.stream().map(Profit::getProfitValue).collect(Collectors.toList()));

	return TradeHelper.addBaseCurrencyProfit(totalProfitProCurrency, currency);
    }

}
