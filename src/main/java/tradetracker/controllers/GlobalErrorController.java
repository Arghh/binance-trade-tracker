package tradetracker.controllers;

import java.util.Date;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import tradetracker.exception.ErrorDetails;

@ControllerAdvice
@RestController
public class GlobalErrorController extends ResponseEntityExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ModelAndView exception(Exception exception, WebRequest request) {
		ModelAndView modelAndView = new ModelAndView("errorpage");
		ErrorDetails errorDetails = new ErrorDetails(new Date(), exception.getMessage(), request.getDescription(false));
		modelAndView.addObject("errorMessage", errorDetails);
		return modelAndView;
	}

}