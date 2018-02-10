package profittracker.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import profittracker.domain.AggregatedTrade;
import profittracker.repositories.AggregatedTradeRepository;
import profittracker.util.TradeHelper;

@Service
public class ProfitServiceImpl implements ProfitService {

	private AggregatedTradeRepository aggTradeRepository;
	private AggregatedTradeService aggTradeService;

	@Autowired
	public ProfitServiceImpl(AggregatedTradeRepository aggTradeRepository, AggregatedTradeService aggTradeService) {
		this.aggTradeRepository = aggTradeRepository;
		this.aggTradeService = aggTradeService;
	}

	@Override
	public List<AggregatedTrade> listAllTradeProfits() {
		List<AggregatedTrade> allTrades = new ArrayList<>();
		// List<String> symbols = aggTradeRepository.findDistinctSymbols();
		List<String> symbols = new ArrayList<>();
		int x = 0;
		for (String s : symbols) {
			allTrades = getTradesBySymbols(s);
		}
		calculateProfitsProSymbol(allTrades);
		if (allTrades.get(x).isBuy()) {
			for (int i = 0; i < allTrades.size(); i++) {
				BigDecimal buyTotal = allTrades.get(i).getTotal();
				BigDecimal sellTotal = allTrades.get(i + 1).getTotal();
				BigDecimal profit = TradeHelper.substractBigDecimals(buyTotal, sellTotal);
			}
		} else {
			System.out.println("Skipping a trade. First trade has to be a buy.");
			x++;
		}

		return allTrades;
	}

	private void calculateProfitsProSymbol(List<AggregatedTrade> allTrades) {
		// TODO Auto-generated method stub

	}

	private List<AggregatedTrade> getTradesBySymbols(String symbol) {
		List<AggregatedTrade> tradesToSymbols = aggTradeRepository.findBySymbol(symbol);
		return tradesToSymbols;

	}

	@Override
	public List<AggregatedTrade> listDailyTradeProfits() {
		// TODO Auto-generated method stub
		return null;
	}

}
