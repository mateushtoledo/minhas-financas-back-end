package com.toledo.minhasfinancas.core;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
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
	public FinancialRecord findById(User u, Long recordId) {
		Optional<FinancialRecord> found = repository.findById(recordId);
		return validateFinancialRecord(found, u, "visualizar");
	}

	@Override
	@Transactional(readOnly = true)
	public Page<FinancialRecord> find(
		@Min(value = 1, message = "O índice da página deve começar em 1.") int pageIndex,
		@Min(value = 1, message = "As páginas devem ter no mínimo 1 registro.") @Max(value = 100, message = "As páginas devem ter no máximo 100 registros.") int pageSize,
		User u,
		String description,
		Integer year,
		Integer month,
		FinancialRecordType type
	) {
		// Criar filtros
		FinancialRecord filters = new FinancialRecord(null, description, month, year, null, type, null, u, null);
		Example<FinancialRecord> ex = Example.of(filters,
			ExampleMatcher.matching()
			.withIgnoreCase()
			.withStringMatcher(StringMatcher.CONTAINING)
		);
		
		// Criar paginação
		List<Order> sortOrder = new ArrayList<>();
		sortOrder.add(new Order(Direction.DESC, "year"));
		sortOrder.add(new Order(Direction.DESC, "month"));
		sortOrder.add(new Order(Direction.ASC, "description"));
		Pageable pagination = PageRequest.of(pageIndex-1, pageSize, Sort.by(sortOrder));
		
		// Pesquisar filtrando e paginando
		return repository.findAll(ex, pagination);
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
	
	@Override
	@Transactional(readOnly = true)
	public Float getSumByUserAndType(User u, FinancialRecordType type) {
		return repository.getSumByUserAndType(u.getId(), type);
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
