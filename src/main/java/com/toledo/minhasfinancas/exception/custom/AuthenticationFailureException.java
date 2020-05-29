package com.toledo.minhasfinancas.exception.custom;

import org.springframework.security.core.AuthenticationException;

public class AuthenticationFailureException extends AuthenticationException {
	private static final long serialVersionUID = 8592842989138585479L;

	public AuthenticationFailureException(String message) {
		super(message);
	}
}
