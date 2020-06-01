package com.toledo.minhasfinancas.exception.custom;

public class FinancialRecordNotFoundException extends RuntimeException {
	private static final long serialVersionUID = -1991615793191152537L;

	public FinancialRecordNotFoundException(String message) {
		super(message);
	}

}
