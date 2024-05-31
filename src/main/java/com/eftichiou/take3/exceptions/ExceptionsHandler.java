package com.eftichiou.take3.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionsHandler {

	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handleException(CustomException exc) {
		ErrorResponse error = new ErrorResponse(exc.getStatusCode().value(), exc.getMessage(),
				System.currentTimeMillis());
		return new ResponseEntity<>(error, exc.getStatusCode());
	}

	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handlerException(RuntimeException exc) {
		ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), exc.getMessage(),
				System.currentTimeMillis());
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

}
