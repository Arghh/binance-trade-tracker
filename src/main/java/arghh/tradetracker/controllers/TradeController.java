package arghh.tradetracker.controllers;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import arghh.tradetracker.exception.ErrorDetails;
import arghh.tradetracker.exception.TradeNotFoundException;
import arghh.tradetracker.model.Trade;
import arghh.tradetracker.services.AggregatedTradeService;
import arghh.tradetracker.services.BinanceApiTradeService;
import arghh.tradetracker.services.ExcelTradeService;
import arghh.tradetracker.services.TradeService;

@Controller
public class TradeController {
	private TradeService tradeService;
	private ExcelTradeService excelService;
	private BinanceApiTradeService apiService;
	private AggregatedTradeService aggService;

	@Autowired
	public void setTradeService(TradeService TradeService, ExcelTradeService excelService,
			BinanceApiTradeService apiService, AggregatedTradeService aggService) {
		this.tradeService = TradeService;
		this.excelService = excelService;
		this.apiService = apiService;
		this.aggService = aggService;
	}

	@RequestMapping("/")
	public ModelAndView mainView() {
		return new ModelAndView("index");
	}

	@RequestMapping("/tools")
	public ModelAndView toolsView() {
		return new ModelAndView("tools");
	}

	@RequestMapping({ "/trade/list/raw" })
	public String listRawTrades(Model model) {
		model.addAttribute("rawTrades", tradeService.listAllRaw());
		return "trade/tradelistraw";
	}

	@RequestMapping({ "/trade/list", "/trade" })
	public String listTrades(Model model) {
		model.addAttribute("trades", aggService.listAllAggregated());
		return "trade/tradelist";
	}

	@RequestMapping({ "/trade/api/save" })
	public String loadAllTradesFromApi() {
		apiService.saveAllBinanaceTrades("WTCBTC");
		return "tools";
	}

	@RequestMapping({ "/trade/excel/save" })
	public String saveAllTradesFromExcel() {
		excelService.saveAllTradesFromFile();
		return "tools";
	}

	@RequestMapping({ "/trade/excel/load" })
	public String saveAllAggTrades() {
		tradeService.saveAllAggregatedTrades();
		return "tools";
	}

	@RequestMapping("/trade/show/{id}")
	public String getTrade(@PathVariable String id, Model model) {
		Trade trade = tradeService.getById(Long.valueOf(id));
		if (trade == null)
			throw new TradeNotFoundException("Trade with the ID: " + id + " not found.");

		model.addAttribute("trade", trade);
		return "trade/show";
	}

	// @RequestMapping("trade/edit/{id}")
	// public String edit(@PathVariable String id, Model model) {
	// Trade Trade = tradeService.getById(Long.valueOf(id));
	// TradeForm TradeForm = TradeToTradeForm.convert(Trade);
	//
	// model.addAttribute("tradeForm", TradeForm);
	// return "trade/tradeform";
	// }
	//
	// @RequestMapping("/trade/new")
	// public String newTrade(Model model) {
	// model.addAttribute("tradeForm", new TradeForm());
	// return "trade/tradeform";
	// }

	// @RequestMapping(value = "/trade", method = RequestMethod.POST)
	// public String saveOrUpdateTrade(@Valid TradeForm TradeForm, BindingResult
	// bindingResult) {
	//
	// if (bindingResult.hasErrors()) {
	// return "trade/tradeform";
	// }
	// // TradeService.saveOrUpdateTradeForm(TradeForm);
	// Trade savedTrade = null;
	//
	// return "redirect:/trade/show/" + savedTrade.getId();
	// }

	@RequestMapping("/trade/delete/{id}")
	public String deleteAggregatedTrade(@PathVariable String id) {
		aggService.delete(Long.valueOf(id));
		return "redirect:/trade/list";
	}

	@RequestMapping("/trade/raw/delete/{id}")
	public String deleteTrade(@PathVariable String id) {
		tradeService.delete(Long.valueOf(id));
		return "redirect:/trade/list/raw";
	}

	@RequestMapping("/trade/raw/truncate")
	public String deleteAllRawTrades() {
		tradeService.deleteAll();
		return "tools";
	}

	@RequestMapping("/trade/truncate")
	public String deleteAllAggTrades() {
		aggService.deleteAll();
		return "tools";
	}

	@ExceptionHandler(TradeNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String handleTradeNotFoundException(TradeNotFoundException ex, WebRequest request, Model model) {
		ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
		model.addAttribute("errorDetails", errorDetails);
		return "errorpage";
	}

	@RequestMapping("/errorpage")
	public String handleAllExceptions(Exception ex, WebRequest request, Model model) {
		ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
		model.addAttribute("errorDetails", errorDetails);
		return "errorpage";
	}

}
