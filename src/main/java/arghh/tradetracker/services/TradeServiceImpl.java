package arghh.tradetracker.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import arghh.tradetracker.converters.TradeToAggTrade;
import arghh.tradetracker.model.AggregatedTrade;
import arghh.tradetracker.model.Trade;
import arghh.tradetracker.repositories.TradeRepository;
import arghh.tradetracker.util.TradeHelper;

@Service
public class TradeServiceImpl implements TradeService {

	private TradeRepository tradeRepository;
	private AggregatedTradeService aggTradeService;
	private TradeToAggTrade aggTradeConverter;

	@Autowired
	public TradeServiceImpl(TradeRepository tradeRepository, AggregatedTradeService aggTradeService,
			TradeToAggTrade aggTradeConverter) {
		this.tradeRepository = tradeRepository;
		this.aggTradeService = aggTradeService;
		this.aggTradeConverter = aggTradeConverter;
	}

	@Override
	public List<Trade> listAllRaw() {
		List<Trade> trades = new ArrayList<>();
		tradeRepository.findAll().forEach(trades::add);

		return trades;
	}

	@Override
	public Trade saveOrUpdate(Trade trade) {
		tradeRepository.save(trade);
		return trade;
	}

	@Override
	public void delete(Long id) {
		tradeRepository.deleteById(id);
	}

	@Override
	public Trade getById(Long id) {
		return tradeRepository.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void saveAllAggregatedTrades() {
		List<String> allSymbols = tradeRepository.findDistinctSymbols();
		List<Trade> rawTrades = new ArrayList<>();
		for (String string : allSymbols) {
			rawTrades.addAll(tradeRepository.findBySymbol(string));
		}

		// TODO: save trade list on aggTrade table to check if already calculated
		List<Trade> newTrades = rawTrades.stream().filter(x -> x.getAggregatedTrade() == null)
				.collect(Collectors.toList());

		if (!newTrades.isEmpty()) {
			System.out.println("Starting task: Combine trades");
			List<AggregatedTrade> aggregatedTrades = combineTrades(newTrades);
			aggTradeService.saveAllAggTrades(aggregatedTrades);
			System.out.println("Completed task: Combine trades");
		} else {
			System.out.println("No new trades to combine found. Please import some new trades first");
		}

	}

	private ArrayList<AggregatedTrade> combineTrades(List<Trade> rawTrades) {
		ArrayList<AggregatedTrade> aggregatedTrades = new ArrayList<>();

		for (int i = 0; i < rawTrades.size();) {
			Trade currentTrade = rawTrades.get(i);

			ArrayList<Trade> partialCanditates = new ArrayList<>();
			partialCanditates.add(currentTrade);

			// check if the next trades might all be part of the current trade
			for (int j = i + 1; j < rawTrades.size(); j++) {

				if ((currentTrade.isBuy() != rawTrades.get(j).isBuy())
						|| (currentTrade.getPrice().compareTo(rawTrades.get(j).getPrice()) != 0)) {
					break;
				}

				partialCanditates.add(rawTrades.get(j));

			}
			if (partialCanditates.size() > 1 && !(checkIfPartialTrade(partialCanditates).isEmpty())) {
				// skip over partial trades
				i = i + partialCanditates.size();

				// add the combined trade of list of partial trades together
				AggregatedTrade combinedTrade = addTradesTogether(partialCanditates);
				aggregatedTrades.add(combinedTrade);
				// -1 for the added combined trade to continue checking at the right position
				i--;
				// remove the partial trades
				for (Trade trade : partialCanditates) {
					aggregatedTrades.remove(aggTradeConverter.convert(trade));
				}

			} else {
				aggregatedTrades.add(aggTradeConverter.convert(currentTrade));
			}
			i++;
		}

		return aggregatedTrades;
	}

	private ArrayList<Trade> checkIfPartialTrade(ArrayList<Trade> partialCanditates) {
		BigDecimal price = partialCanditates.get(0).getPrice();

		// TODO: crtieria for partial trade next sell quantity = all buy quantities or
		// reversed
		boolean partialTrade = partialCanditates.stream().allMatch((x) -> x.getPrice().compareTo(price) == 0);

		if (partialTrade) {
			System.out.println("The last " + partialCanditates.size()
					+ " trades look like partial trades. Adding trades together.");
			return partialCanditates;
		}

		return new ArrayList<>();
	}

	private AggregatedTrade addTradesTogether(ArrayList<Trade> partialTrades) {
		Trade lastTrade = partialTrades.get(partialTrades.size() - 1);
		AggregatedTrade combinedTrade = new AggregatedTrade();
		List<BigDecimal> totalQuantity = new ArrayList<>();
		List<BigDecimal> totalCost = new ArrayList<>();
		List<BigDecimal> totalFee = new ArrayList<>();
		combinedTrade.setTradeTime(lastTrade.getTradeTime());
		combinedTrade.setSymbol(lastTrade.getSymbol());
		combinedTrade.setBuy(lastTrade.isBuy());
		combinedTrade.setPrice(lastTrade.getPrice());
		combinedTrade.setFeeCoin(lastTrade.getFeeCoin());

		partialTrades.forEach(t -> {
			totalCost.add(t.getTotal());
			totalQuantity.add(t.getQuantity());
			totalFee.add(t.getFee());
		});

		combinedTrade.setTrade(partialTrades);
		combinedTrade.setQuantity(TradeHelper.addBigDecimals(totalQuantity));
		combinedTrade.setTotal(TradeHelper.addBigDecimals(totalCost));
		combinedTrade.setFee(TradeHelper.addBigDecimals(totalFee));
		return combinedTrade;
	}

	@Override
	public void deleteAll() {
		tradeRepository.deleteAll();
		System.out.println("Deleted all Excel imports");
	}

}
