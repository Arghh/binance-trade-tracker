package arghh.tradetracker.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import arghh.tradetracker.converters.ExcelTradeToTrade;
import arghh.tradetracker.model.Trade;

@Service
public class ExcelTradeServiceImpl implements ExcelTradeService {

    private ExcelTradeToTrade excelTradeToTrade;
    private TradeService tradeService;

    @Value("${tradeHistory.path}")
    private String filePath;

    @Autowired
    public ExcelTradeServiceImpl(ExcelTradeToTrade productFormToProduct, TradeService tradeService) {
	excelTradeToTrade = productFormToProduct;
	this.tradeService = tradeService;
    }

    @Override
    public List<Trade> saveAllTradesFromFile() {
	try (FileInputStream file = new FileInputStream(new File(filePath))) {

	    Workbook workbook = new XSSFWorkbook(file);
	    Sheet sheet = workbook.getSheetAt(0);

	    List<String> excelData = new ArrayList<>();
	    List<Trade> rawTrades = new ArrayList<>();
	    for (Row row : sheet) {
		for (Cell cell : row) {
		    switch (cell.getCellType()) {
		    case STRING:
			excelData.add(cell.getStringCellValue());
			break;
		    default:
			excelData.add(" ");
		    }
		}
		Trade newTrade = excelTradeToTrade.convert(excelData);

		if (newTrade != null) {
		    rawTrades.add(newTrade);
		}
		System.out.println(excelData);

		excelData.clear();
	    }
	    // sort all imported trades so we get the oldest first
	    sortExcelTradesBeforeSave(rawTrades);
	    workbook.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return null;
    }

    @Override
    @Transactional
    public Trade saveNewExcelTrade(Trade newTradeToSave) {
	if (newTradeToSave == null) {
	    System.out.println("Could not save a new trade");
	    return newTradeToSave;
	}
	newTradeToSave = tradeService.saveOrUpdate(newTradeToSave);
	System.out.println(MessageFormat.format("Saved a trade from Excel with the Symbol {0} and total amount {1}",
		newTradeToSave.getSymbol(), newTradeToSave.getTotal()));

	return newTradeToSave;
    }

    private void sortExcelTradesBeforeSave(List<Trade> rawTrades) {

	// sort list. TODO: lambda ala list.sort(Comparator.comparing(o ->
	// o.getDateTime()));
	Collections.sort(rawTrades, (o1, o2) -> {
	    if (o1.getTradeTime() == null || o2.getTradeTime() == null) {
		System.out.println("Trade times are not set.");
		return 0;
	    }
	    if (o1.getTradeTime().compareTo(o2.getTradeTime()) == 0) {
		System.out.println("Trade time is same the same. Saving buy first");
		return -1;
	    }
	    return o1.getTradeTime().compareTo(o2.getTradeTime());
	});
	System.out.println("Starting task: Excel import");
	rawTrades.forEach(this::saveNewExcelTrade);
	System.out.println("Completed task: Excel import");
    }

}
