package arghh.tradetracker.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import arghh.tradetracker.commands.ProfitList;
import arghh.tradetracker.services.AggregatedTradeService;
import arghh.tradetracker.services.ExcelTradeService;
import arghh.tradetracker.services.ProfitService;

@Controller
public class ProfitController {
	private AggregatedTradeService tradeService;
	private ExcelTradeService excelService;
	private ProfitService profitService;

	@Autowired
	public void setTradeService(AggregatedTradeService TradeService, ExcelTradeService excelService,
			ProfitService profitService) {
		this.tradeService = TradeService;
		this.excelService = excelService;
		this.profitService = profitService;
	}

	@RequestMapping({ "/profit/list", "/profit/" })
	public String listAllProfits(Model model) {
		model.addAttribute("profits", profitService.showAllProfits());
		model.addAttribute("totalProfits", profitService.totalProfits());
		return "profit/profitlist";
	}

	@RequestMapping({ "/profit/list/daily" })
	public String listDailyProfits(Model model, @RequestParam(value = "date", required = false) String date) {

		// if (date == null) {
		// System.out.println("date null");
		// }
		List<String> totalProfitsDaily = profitService.totalDailyProfits("asd");
		List<ProfitList> profitsDaily = profitService.listDailyTradeProfits("asd");

		model.addAttribute("totalProfitsDaily", totalProfitsDaily);
		model.addAttribute("profitsDaily", profitsDaily);
		return "profit/profitlistdaily";
	}

	@RequestMapping({ "/profit/save" })
	public String saveAllProfitsFromTrades() {
		profitService.saveAllTradeProfits();
		return "tools";
	}

	@RequestMapping({ "/profit/truncate" })
	public String truncateProfits() {
		profitService.deleteAll();
		return "tools";
	}

}
