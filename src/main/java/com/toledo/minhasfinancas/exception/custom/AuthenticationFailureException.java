package com.toledo.minhasfinancas.exception.custom;

public class AuthenticationFailureException extends RuntimeException {
	private static final long serialVersionUID = 8592842989138585479L;

	public AuthenticationFailureException(String message) {
		super(message);
	}
}
