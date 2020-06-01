package com.toledo.minhasfinancas.core;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.toledo.minhasfinancas.domain.FinancialRecord;
import com.toledo.minhasfinancas.domain.User;
import com.toledo.minhasfinancas.domain.enums.FinancialRecordStatus;
import com.toledo.minhasfinancas.domain.enums.FinancialRecordType;
import com.toledo.minhasfinancas.exception.custom.BusinessAuthorizationException;
import com.toledo.minhasfinancas.exception.custom.FinancialRecordNotFoundException;
import com.toledo.minhasfinancas.port.inbound.FinancialRecordServicePort;
import com.toledo.minhasfinancas.repository.FinancialRecordRepository;

@Service
@Validated
public class FinancialRecordService implements FinancialRecordServicePort {
	private FinancialRecordRepository repository;
	
	@Autowired
	public FinancialRecordService(FinancialRecordRepository repository) {
		super();
		this.repository = repository;
	}

	@Override
	@Transactional
	public FinancialRecord save(User u, @Valid FinancialRecord recordData) {
		recordData.setId(null);
		recordData.setUser(u);
		recordData.setStatus(FinancialRecordStatus.PENDANT);
		recordData.setRegisterDate(LocalDate.now());
		
		return repository.save(recordData);
	}

	@Override
	@Transactional
	public FinancialRecord update(User u, Long recordId, @Valid FinancialRecord recordData) {
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
	@Transactional(readOnly = true)
	public List<FinancialRecord> find(User u, String description, Integer year, Integer month, FinancialRecordType type) {
		FinancialRecord filters = new FinancialRecord(null, description, month, year, null, type, null, u, null);
		Example<FinancialRecord> ex = Example.of(filters,
			ExampleMatcher.matching()
			.withIgnoreCase()
			.withStringMatcher(StringMatcher.CONTAINING)
		);
		
		return repository.findAll(ex);
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
