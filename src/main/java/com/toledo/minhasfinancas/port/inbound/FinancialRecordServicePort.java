package com.toledo.minhasfinancas.port.inbound;

import java.util.List;

import com.toledo.minhasfinancas.domain.FinancialRecord;
import com.toledo.minhasfinancas.domain.User;
import com.toledo.minhasfinancas.domain.enums.FinancialRecordStatus;
import com.toledo.minhasfinancas.domain.enums.FinancialRecordType;

public interface FinancialRecordServicePort {
	public FinancialRecord save(User u, FinancialRecord recordData);
	
	public FinancialRecord update(User u, Long recordId, FinancialRecord recordData);
	
	public List<FinancialRecord> find(User u, Integer year, Integer month, FinancialRecordType type);
	
	public FinancialRecord updateStatus(User u, Long recordId, FinancialRecordStatus status);
	
	public void delete(User u, Long recordId);
}
