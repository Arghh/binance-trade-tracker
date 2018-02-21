package arghh.tradetracker.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crypto.cryptocompare.api.CryptoCompareApi;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class CryptoCompareApiServiceImpl implements CryptoCompareApiService {

	private CryptoCompareApi api = new CryptoCompareApi();
	private TradeService tradeService;

	@Autowired
	public CryptoCompareApiServiceImpl(TradeService tradeService) {
		this.tradeService = tradeService;
	}

	@Override
	public BigDecimal getCurrentValueInUsd(String symbolAndValue) {
		String[] splited = symbolAndValue.split("\\s+");
		String usd = "USD";
		try {

			JsonObject response = api.price(splited[1], usd, new HashMap<String, Object>());
			if (response.isJsonNull()) {
				System.out.println("Problems getting USD price from CryptoCompare");
				return null;
			}

			List<Entry<String, JsonElement>> data = response.entrySet().stream().collect(Collectors.toList());

			BigDecimal currentValue = data.get(0).getValue().getAsBigDecimal();

			BigDecimal valueInUsd = new BigDecimal(splited[0]).multiply(currentValue);

			valueInUsd = valueInUsd.setScale(2, RoundingMode.CEILING);

			return valueInUsd;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

}
