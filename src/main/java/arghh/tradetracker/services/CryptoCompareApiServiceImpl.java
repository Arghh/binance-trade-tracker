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

@Service
public class CryptoCompareApiServiceImpl implements CryptoCompareApiService {

    private CryptoCompareApi api = new CryptoCompareApi();
    @Value("${preferedFiat}")
    private String preferedFiat;

    private TradeService tradeService;

    @Autowired
    public CryptoCompareApiServiceImpl(TradeService tradeService) {
	this.tradeService = tradeService;
    }

    private String checkIfFiatIsCorrect(String preferedFiat) {
	for (BaseFiat fiat : BaseFiat.values()) {
	    if (preferedFiat.equals(fiat.toString())) {
		return preferedFiat;
	    }
	}

	return null;
    }

    @Override
    public String getCurrentValueInFiat(String symbolAndValue) {

	try {
	    var splited = symbolAndValue.split("\\s+");
	    var fiat = checkIfFiatIsCorrect(preferedFiat);

	    if (fiat == null) {
		System.out.println(
			"The choosen currency is not set or not supported. Please choose EUR or USD in your properties. (preferedFiat)");
		return null;
	    }
	    var result = "";

	    var response = api.price(splited[1], fiat, new HashMap<String, Object>());
	    if (response.isJsonNull()) {
		System.out.println("Problems getting the " + fiat + " price from CryptoCompare");
		return null;
	    }

	    List<Entry<String, JsonElement>> data = response.entrySet().stream().collect(Collectors.toList());

	    var currentValue = data.get(0).getValue().getAsBigDecimal();

	    var valueInUsd = new BigDecimal(splited[0]).multiply(currentValue);

	    valueInUsd = valueInUsd.setScale(2, RoundingMode.CEILING);

	    if ("EUR".equals(fiat)) {
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
	    var splited = symbolAndValue.split("\\s+");

	    var fiat = checkIfFiatIsCorrect(preferedFiat);

	    if (fiat == null) {
		System.out.println(
			"The choosen currency is not set or not supported. Please choose EUR or USD in your properties. (preferedFiat)");
		return null;
	    }
	    var optionalParams = new LinkedHashMap<String, Object>();

	    // /1000 because unix timestamp
	    optionalParams.put("toTs", date.getTime() / 1000);
	    optionalParams.put("extraParams", "binaceTradeTracker");

	    var response = api.dayAvg(splited[1], fiat, optionalParams);
	    if (response.isJsonNull()) {
		System.out.println("Problems getting Historical " + fiat + " price from CryptoCompare");
		return null;
	    }
	    // response that we get is {ETH: { value : USD } }
	    List<Entry<String, JsonElement>> data = response.entrySet().stream().collect(Collectors.toList());

	    var currentValue = data.get(0).getValue().getAsBigDecimal();
	    var valueInUsd = new BigDecimal(splited[0]).multiply(currentValue);
	    valueInUsd = valueInUsd.setScale(2, RoundingMode.CEILING);

	    var result = "";
	    if ("EUR".equals(fiat)) {
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

}
