package com.toledo.minhasfinancas.port.inbound;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.springframework.data.domain.Page;

import com.toledo.minhasfinancas.domain.FinancialRecord;
import com.toledo.minhasfinancas.domain.User;
import com.toledo.minhasfinancas.domain.enums.FinancialRecordStatus;
import com.toledo.minhasfinancas.domain.enums.FinancialRecordType;

public interface FinancialRecordServicePort {
	public FinancialRecord save(User u, @Valid FinancialRecord recordData);
	
	public FinancialRecord update(User u, Long recordId, @Valid FinancialRecord recordData);
	
	public Page<FinancialRecord> find(
		@Min(value = 1, message = "O índice da página deve começar em 1.") int pageIndex,
		@Min(value = 1, message = "As páginas devem ter no mínimo 1 registro.") @Max(value = 100, message = "As páginas devem ter no máximo 100 registros.") int pageSize,
		User u,
		String description,
		Integer year,
		Integer month,
		FinancialRecordType type
	);
	
	public FinancialRecord findById(User u, Long recordId);
	
	public FinancialRecord updateStatus(User u, Long recordId, FinancialRecordStatus status);
	
	public void delete(User u, Long recordId);
	
	public Float getSumByUserAndType(User u, FinancialRecordType type);
}
