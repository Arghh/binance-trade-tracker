package tradetracker.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import tradetracker.services.AggregatedTradeService;
import tradetracker.services.ExcelTradeService;
import tradetracker.services.ProfitService;

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
		model.addAttribute("profits", profitService.listAllProfits());
		return "profit/list";
	}

	@RequestMapping({ "/profit/list/daily" })
	public String listDailyProfits(Model model) {
		model.addAttribute("profits", profitService.listDailyTradeProfits());
		return "profit/list";
	}

	@RequestMapping({ "/profit/save" })
	public String saveAllProfitsFromTrades() {
		profitService.saveAllTradeProfits();
		return "profit/list";
	}

}
