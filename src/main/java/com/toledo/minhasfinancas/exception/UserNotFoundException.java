package com.toledo.minhasfinancas.exception;

public class UserNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 8155790840745528553L;

	public UserNotFoundException(String message) {
		super(message);
	}

}
