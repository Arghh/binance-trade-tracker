package arghh.tradetracker.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.crypto.cryptocompare.api.CryptoCompareApi;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class CryptoCompareApiServiceImpl implements CryptoCompareApiService {

	private CryptoCompareApi api = new CryptoCompareApi();
	private TradeService tradeService;

	@Value("${preferedFiat}")
	private String preferedFiat;

	@Autowired
	public CryptoCompareApiServiceImpl(TradeService tradeService) {
		this.tradeService = tradeService;
	}

	@Override
	public String getCurrentValueInFiat(String symbolAndValue) {

		try {
			String[] splited = symbolAndValue.split("\\s+");
			String fiat = checkIfFiatIsCorrect(preferedFiat);

			if (fiat == null) {
				System.out.println(
						"The choosen currency is not set or not supported. Please choose EUR or USD in your properties. (preferedFiat)");
				return null;
			}
			String result = "";
			JsonObject response = api.price(splited[1], fiat, new HashMap<String, Object>());
			if (response.isJsonNull()) {
				System.out.println("Problems getting the " + fiat + " price from CryptoCompare");
				return null;
			}

			List<Entry<String, JsonElement>> data = response.entrySet().stream().collect(Collectors.toList());

			BigDecimal currentValue = data.get(0).getValue().getAsBigDecimal();

			BigDecimal valueInUsd = new BigDecimal(splited[0]).multiply(currentValue);

			valueInUsd = valueInUsd.setScale(2, RoundingMode.CEILING);

			if (fiat.equals("EUR")) {
				result = valueInUsd.stripTrailingZeros().toPlainString() + " €";
			} else {
				result = valueInUsd.stripTrailingZeros().toPlainString() + " $";
			}
			return result;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	@Override
	public String getHistoricalDailyValue(String symbolAndValue, Date date) {

		try {
			String[] splited = symbolAndValue.split("\\s+");

			String fiat = checkIfFiatIsCorrect(preferedFiat);

			if (fiat == null) {
				System.out.println(
						"The choosen currency is not set or not supported. Please choose EUR or USD in your properties. (preferedFiat)");
				return null;
			}
			LinkedHashMap<String, Object> optionalParams = new LinkedHashMap<>();

			// /1000 because unix timestamp
			optionalParams.put("toTs", date.getTime() / 1000);
			optionalParams.put("extraParams", "binaceTradeTracker");

			JsonObject response = api.dayAvg(splited[1], fiat, optionalParams);
			if (response.isJsonNull()) {
				System.out.println("Problems getting Historical " + fiat + " price from CryptoCompare");
				return null;
			}
			// response that we get is {ETH: { value : USD } }
			List<Entry<String, JsonElement>> data = response.entrySet().stream().collect(Collectors.toList());

			BigDecimal currentValue = data.get(0).getValue().getAsBigDecimal();
			BigDecimal valueInUsd = new BigDecimal(splited[0]).multiply(currentValue);
			valueInUsd = valueInUsd.setScale(2, RoundingMode.CEILING);

			String result = "";
			if (fiat.equals("EUR")) {
				result = valueInUsd.stripTrailingZeros().toPlainString() + " €";
			} else {
				result = valueInUsd.stripTrailingZeros().toPlainString() + " $";
			}
			return result;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	private String checkIfFiatIsCorrect(String preferedFiat) {
		for (BaseFiat fiat : BaseFiat.values()) {
			if (preferedFiat.equals(fiat.toString())) {
				return preferedFiat;
			}
		}

		return null;
	}

}
