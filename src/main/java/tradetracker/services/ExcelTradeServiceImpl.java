package tradetracker.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import tradetracker.converters.ExcelTradeToTrade;
import tradetracker.model.Trade;

@Service
public class ExcelTradeServiceImpl implements ExcelTradeService {

	private ExcelTradeToTrade excelTradeToTrade;
	private TradeService tradeService;
	private AggregatedTradeService aggService;

	@Value("${tradeHistory.path}")
	private String filePath;

	@Autowired
	public ExcelTradeServiceImpl(ExcelTradeToTrade productFormToProduct, TradeService tradeService,
			AggregatedTradeService aggService) {
		this.excelTradeToTrade = productFormToProduct;
		this.tradeService = tradeService;
		this.aggService = aggService;
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
					switch (cell.getCellTypeEnum()) {
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
			workbook.close();
			sortExcelTradesBeforeSave(rawTrades);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Trade saveNewExcelTrade(Trade newTradeToSave) {
		if (newTradeToSave != null) {
			newTradeToSave = tradeService.saveOrUpdate(newTradeToSave);
			System.out.println("Saved a trade from Excel with the Symbol: " + newTradeToSave.getSymbol()
					+ " and price of: " + newTradeToSave.getTotal());
		} else {
			System.out.println("Could not save a new trade");
			return newTradeToSave;
		}

		return newTradeToSave;
	}

	private void sortExcelTradesBeforeSave(List<Trade> rawTrades) {

		// sort list. TODO: lambda ala list.sort(Comparator.comparing(o ->
		// o.getDateTime()));
		Collections.sort(rawTrades, new Comparator<Trade>() {
			@Override
			public int compare(Trade o1, Trade o2) {
				if (o1.getTimeOfTrade() == null || o2.getTimeOfTrade() == null)
					return 0;
				return o1.getTimeOfTrade().compareTo(o2.getTimeOfTrade());
			}
		});

		rawTrades.forEach(t -> saveNewExcelTrade(t));
	}

}
