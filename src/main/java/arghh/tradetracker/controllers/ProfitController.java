package arghh.tradetracker.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import arghh.tradetracker.commands.ProfitList;
import arghh.tradetracker.services.ProfitService;

@Controller
public class ProfitController {
	private ProfitService profitService;

	@Autowired
	public void setTradeService(ProfitService profitService) {
		this.profitService = profitService;
	}

	@RequestMapping({ "/profit/list", "/profit/" })
	public String listAllProfits(Model model) {
		model.addAttribute("profits", profitService.showAllProfits());
		model.addAttribute("totalProfits", profitService.allTimeProfits());
		return "profit/profitlist";
	}

	@RequestMapping({ "/profit/list/daily" })
	public String listDailyProfits(Model model, @RequestParam(value = "date", required = false) String day) {
		List<String> totalProfitsDaily = new ArrayList<>();
		List<ProfitList> profitsDaily = new ArrayList<>();
		if (day == null || day.isEmpty()) {
			System.out.println("Date not set");
			model.addAttribute("totalProfitsDaily", totalProfitsDaily);
			model.addAttribute("profitsDaily", profitsDaily);
			return "profit/profitlistdaily";
		}
		totalProfitsDaily = profitService.calculatedTotalDailyProfits(day);
		profitsDaily = profitService.listDailyTradeProfits(day);

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

	@RequestMapping("/profit/delete/{id}")
	public String delete(@PathVariable String id) {
		profitService.delete(Long.valueOf(id));
		return "redirect:/profit/list";
	}

}
