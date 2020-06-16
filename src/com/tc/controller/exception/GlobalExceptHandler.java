package com.tc.controller.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptHandler {

	@ExceptionHandler(Exception.class)
	public String exceptHand(Exception ex){
		ex.printStackTrace();
		return "/error/500.jsp";
	}
	
}
