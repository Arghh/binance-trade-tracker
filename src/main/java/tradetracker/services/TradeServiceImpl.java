package tradetracker.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tradetracker.model.Trade;
import tradetracker.repositories.TradeRepository;
import tradetracker.util.TradeHelper;

@Service
public class TradeServiceImpl implements TradeService {

	private TradeRepository tradeRepository;
	private AggregatedTradeService aggTradeService;

	@Autowired
	public TradeServiceImpl(TradeRepository tradeRepository, AggregatedTradeService aggTradeService) {
		this.tradeRepository = tradeRepository;
		this.aggTradeService = aggTradeService;
	}

	@Override
	public List<Trade> listAllRaw() {
		List<Trade> trades = new ArrayList<>();
		tradeRepository.findAll().forEach(trades::add); // fun with Java 86

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
	public void saveAllAggregatedTrades() {
		ArrayList<Trade> rawTrades = (ArrayList<Trade>) listAllRaw();
		ArrayList<Trade> aggregatedTrades = combineTrades(rawTrades);
		aggTradeService.convertAndSaveAllAggTrades(aggregatedTrades);
	}

	private ArrayList<Trade> combineTrades(ArrayList<Trade> rawTrades) {
		ArrayList<Trade> aggregatedTrades = new ArrayList<>();
		// Trade lastTrade = new Trade();
		for (int i = 0; i < rawTrades.size();) {
			Trade currentTrade = rawTrades.get(i);
			// if (lastTrade.getId() != null) {
			ArrayList<Trade> partialCanditates = new ArrayList<>();
			partialCanditates.add(currentTrade);

			// check if the next trades might all be part of the current trade
			for (int j = i + 1; j < rawTrades.size(); j++) {
				System.out.println(currentTrade.getId());
				System.out.println(rawTrades.get(j).getId());
				if (currentTrade.isBuy() != rawTrades.get(j).isBuy()
						|| currentTrade.getPrice().compareTo(rawTrades.get(j).getPrice()) != 0) {
					break;
				}
				partialCanditates.add(rawTrades.get(j));

			}
			if (partialCanditates.size() > 1 && !(checkIfPartialTrade(partialCanditates).isEmpty())) {
				// skip over partial trades
				i = i + partialCanditates.size();

				// add the combined trade of list of partial trades together
				Trade combinedTrade = addTradesTogether(partialCanditates);
				aggregatedTrades.add(combinedTrade);
				// -1 for the added combined trade to continue checking at the right position
				i--;
				// remove the partial trades
				aggregatedTrades.removeAll(partialCanditates);
			} else {
				aggregatedTrades.add(currentTrade);
			}
			i++;
		}

		// else {
		// aggregatedTrades.add(trade);
		// }

		// lastTrade = trade;

		// }

		return aggregatedTrades;
	}

	private ArrayList<Trade> checkIfPartialTrade(ArrayList<Trade> partialCanditates) {
		BigDecimal price = partialCanditates.get(0).getPrice();
		long date = partialCanditates.get(0).getTradeTime().getTime();

		boolean partialTrade = partialCanditates.stream().allMatch(
				(x) -> x.getPrice().compareTo(price) == 0 && (x.getTradeTime().getTime() - date) / 1000 < 10);

		// if (trade.getSymbol().equals(lastTrade.getSymbol())) {
		// System.out.println("The last two trades have the same symbol: " +
		// trade.getSymbol());
		// if (trade.getPrice().equals(lastTrade.getPrice())) {
		// System.out.println("The last two trades have the same fee symbol.");
		// long seconds = Math.abs(trade.getTimeOfTrade().getTime() -
		// lastTrade.getTimeOfTrade().getTime()) / 1000;
		// if (seconds < 10) {
		// partialTrade = true;
		// System.out.println("The last two trades took place only " + seconds
		// + " seconds appart from eachother. Adding two partial trades together.");
		// }
		//
		// }
		// }
		if (partialTrade) {
			System.out.println("The last " + partialCanditates.size()
					+ " trades took place only seconds appart from eachother. Adding partial trades together.");
			return partialCanditates;
		}

		return new ArrayList<>();
	}

	private Trade addTradesTogether(ArrayList<Trade> partialTrades) {
		Trade firstTrade = partialTrades.get(0);
		Trade combinedTrade = new Trade();
		List<BigDecimal> totalQuantity = new ArrayList<>();
		List<BigDecimal> totalCost = new ArrayList<>();
		List<BigDecimal> totalFee = new ArrayList<>();
		combinedTrade.setTradeTime(firstTrade.getTradeTime());
		combinedTrade.setSymbol(firstTrade.getSymbol());
		combinedTrade.setBuy(firstTrade.isBuy());
		combinedTrade.setPrice(firstTrade.getPrice());
		combinedTrade.setFeeCoin(firstTrade.getFeeCoin());

		partialTrades.forEach(t -> {
			totalCost.add(t.getTotal());
			totalQuantity.add(t.getQuantity());
			totalFee.add(t.getFee());
		});

		combinedTrade.setQuantity(TradeHelper.addBigDecimals(totalQuantity));
		combinedTrade.setTotal(TradeHelper.addBigDecimals(totalCost));
		combinedTrade.setFee(TradeHelper.addBigDecimals(totalFee));
		return combinedTrade;
	}

}
