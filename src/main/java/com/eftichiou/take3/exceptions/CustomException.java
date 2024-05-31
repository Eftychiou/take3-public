package com.eftichiou.take3.exceptions;

import org.springframework.http.HttpStatus;

@SuppressWarnings("serial")
public class CustomException extends RuntimeException {
	

	private HttpStatus statusCode;

	public CustomException() {

	}

	public CustomException(String message,HttpStatus statusCode) {
		
		super(message);
		this.statusCode = statusCode;	
		

	}

	public HttpStatus getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(HttpStatus statusCode) {
		this.statusCode = statusCode;
	}

	public CustomException(Throwable cause) {
		super(cause);

	}

	public CustomException(String message, Throwable cause) {
		super(message, cause);

	}

	public CustomException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);

	}

}
