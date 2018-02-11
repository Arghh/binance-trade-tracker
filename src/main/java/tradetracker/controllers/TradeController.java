package tradetracker.controllers;

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

import tradetracker.exception.ErrorDetails;
import tradetracker.exception.TradeNotFoundException;
import tradetracker.model.Trade;
import tradetracker.services.AggregatedTradeService;
import tradetracker.services.BinanceApiTradeService;
import tradetracker.services.ExcelTradeService;
import tradetracker.services.TradeService;

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

	@RequestMapping({ "/trade/list/raw" })
	public String listRawTrades(Model model) {
		model.addAttribute("trades", tradeService.listAllRaw());
		return "trade/list";
	}

	@RequestMapping({ "/trade/list", "/trade" })
	public String listTrades(Model model) {
		model.addAttribute("trades", aggService.listAllAggregated());
		return "trade/list";
	}

	@RequestMapping({ "/trade/api/save" })
	public String loadAllTradesFromApi() {
		apiService.saveAllBinanaceTrades("WTCBTC");
		return "trade/list";
	}

	@RequestMapping({ "/trade/excel/save" })
	public String saveAllTradesFromExcel() {
		excelService.saveAllTradesFromFile();
		return "trade/list";
	}

	@RequestMapping({ "/trade/excel/load" })
	public String saveAllAggTrades() {
		tradeService.saveAllAggregatedTrades();
		return "trade/list";
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
	public String delete(@PathVariable String id) {
		tradeService.delete(Long.valueOf(id));
		return "trade/list";
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
