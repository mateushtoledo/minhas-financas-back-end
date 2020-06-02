package com.toledo.minhasfinancas.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FinancialExtractDTO {
	private Float incomes, expenses, balance;
	
	public FinancialExtractDTO(Float incomes, Float expenses) {
		this.incomes = incomes == null ?  0f : incomes;
		this.expenses = expenses == null ? 0f : expenses;
		this.balance = Float.sum(this.incomes, this.expenses*-1);
	}
}
