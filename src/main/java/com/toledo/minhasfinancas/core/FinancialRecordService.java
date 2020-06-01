package com.toledo.minhasfinancas.core;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.toledo.minhasfinancas.domain.FinancialRecord;
import com.toledo.minhasfinancas.domain.User;
import com.toledo.minhasfinancas.domain.enums.FinancialRecordStatus;
import com.toledo.minhasfinancas.domain.enums.FinancialRecordType;
import com.toledo.minhasfinancas.exception.custom.BusinessAuthorizationException;
import com.toledo.minhasfinancas.exception.custom.FinancialRecordNotFoundException;
import com.toledo.minhasfinancas.port.inbound.FinancialRecordServicePort;
import com.toledo.minhasfinancas.repository.FinancialRecordRepository;

@Service
public class FinancialRecordService implements FinancialRecordServicePort {
	private FinancialRecordRepository repository;
	
	@Autowired
	public FinancialRecordService(FinancialRecordRepository repository) {
		super();
		this.repository = repository;
	}

	@Override
	@Transactional
	public FinancialRecord save(User u, FinancialRecord recordData) {
		recordData.setId(null);
		recordData.setUser(u);
		recordData.setStatus(FinancialRecordStatus.PENDANT);
		recordData.setRegisterDate(LocalDate.now());
		
		return repository.save(recordData);
	}

	@Override
	@Transactional
	public FinancialRecord update(User u, Long recordId, FinancialRecord recordData) {
		// Load and validate financial record
		Optional<FinancialRecord> found = repository.findById(recordId);
		FinancialRecord record = validateFinancialRecord(found, u, "alterar");
		
		// Change allowed fields
		record.setDescription(recordData.getDescription());
		record.setMonth(recordData.getMonth());
		record.setType(recordData.getType());
		record.setValue(recordData.getValue());
		record.setYear(recordData.getYear());
		
		// persist changes
		return repository.save(record);
	}

	@Override
	public List<FinancialRecord> find(User u, Integer year, Integer month, FinancialRecordType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional
	public FinancialRecord updateStatus(User u, Long recordId, FinancialRecordStatus status) {
		// Load and validate record
		Optional<FinancialRecord> found = repository.findById(recordId);
		FinancialRecord record = validateFinancialRecord(found, u, "alterar o status de");
		
		// Skip update if it's not necessary
		if (record.getStatus().equals(status)) {
			return record;
		}
		
		// Update status and persist changes
		record.setStatus(status);
		return repository.save(record);
	}

	@Override
	@Transactional
	public void delete(User u, Long recordId) {
		// Load and validate record
		Optional<FinancialRecord> found = repository.findById(recordId);
		validateFinancialRecord(found, u, "apagar");
		
		// Delete permanently
		repository.deleteById(recordId);
	}
	
	private FinancialRecord validateFinancialRecord(Optional<FinancialRecord> found, User requester, String operationKeyword) {
		if (!found.isPresent()) {
			throw new FinancialRecordNotFoundException("O registro financeiro não foi encontrado!");
		}
		
		FinancialRecord persistentData = found.get();
		if (!persistentData.getUser().getId().equals(requester.getId())) {
			throw new BusinessAuthorizationException("Você não pode " + operationKeyword + " um registro financeiro de outra pessoa!");
		}
		
		return persistentData;
	}
}
