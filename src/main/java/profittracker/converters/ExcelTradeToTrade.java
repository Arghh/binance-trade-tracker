package profittracker.converters;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import profittracker.domain.Trade;
import profittracker.exception.TradeException;
import profittracker.repositories.TradeRepository;

@Component
public class ExcelTradeToTrade implements Converter<List<String>, Trade> {

	private TradeRepository tradeRepository;

	@Autowired
	public ExcelTradeToTrade(TradeRepository tradeRepository) {
		this.tradeRepository = tradeRepository;
	}

	@Override
	public Trade convert(List<String> source) {
		try {
			if (source.size() != 8) {
				System.out.println("To many cells in " + source + " this row.");
				return null;
			}

			Trade trade = new Trade();
			trade.setTimeOfTrade(convertDate(source.get(0)));
			trade.setSymbol(source.get(1));
			trade.setBuy(convertBuy(source.get(2)));
			trade.setPrice(convertDecimal(source.get(3)));
			trade.setQuantity(convertDecimal(source.get(4)));
			trade.setTotal(convertDecimal(source.get(5)));
			trade.setFee(convertDecimal(source.get(6)));
			trade.setFeeCoin(source.get(7));

			return trade;
		} catch (TradeException e) {
			System.out.println(e.getMessage());

		}

		return null;

	}

	private Date convertDate(String cell) throws TradeException {
		if (cell != null && cell.startsWith("2")) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
			try {
				return formatter.parse(cell);
			} catch (ParseException e) {
				System.out.println(e.getMessage());
			}
		} else {
			throw new TradeException(
					"The Excel sheet you have entered is not correct. Can't convert " + cell + " to any value.");
		}
		return null;
	}

	private boolean convertBuy(String cell) {
		return cell.toUpperCase().equals("BUY");
	}

	private BigDecimal convertDecimal(String cell) {
		String filteredString = cell.replaceAll(",", ".");
		BigDecimal amount = new BigDecimal(filteredString);
		return amount;
	}
}
