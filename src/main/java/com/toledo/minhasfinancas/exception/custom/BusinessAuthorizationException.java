package com.toledo.minhasfinancas.exception.custom;


public class BusinessAuthorizationException extends RuntimeException {
	private static final long serialVersionUID = 6211959446371020245L;

	public BusinessAuthorizationException(String message) {
		super(message);
	}
}
