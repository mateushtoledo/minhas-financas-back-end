package com.toledo.minhasfinancas.port.inbound;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.toledo.minhasfinancas.domain.FinancialRecord;
import com.toledo.minhasfinancas.domain.User;
import com.toledo.minhasfinancas.domain.enums.FinancialRecordStatus;
import com.toledo.minhasfinancas.domain.enums.FinancialRecordType;
import com.toledo.minhasfinancas.exception.custom.BusinessAuthorizationException;
import com.toledo.minhasfinancas.exception.custom.FinancialRecordNotFoundException;
import com.toledo.minhasfinancas.repository.FinancialRecordRepository;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class FinancialRecordServicePortTest {
	@SpyBean
	private FinancialRecordServicePort service;
	@MockBean
	private FinancialRecordRepository repository;

	@Test
	public void saveFinancialRecord() {
		// Scenario
		FinancialRecord toSave = buildRecord();
		FinancialRecord saved = buildRecordWithIdAndUser();
		when(repository.save(toSave)).thenReturn(saved);

		// Action
		FinancialRecord record = service.save(buildUser(1L), toSave);

		// Verification
		assertThat(record.getId()).isEqualTo(saved.getId());
		assertThat(record.getStatus()).isEqualTo(FinancialRecordStatus.PENDANT);
	}

	@Test
	public void testSaveInvalidFinancialRecord() {
		// Scenario
		FinancialRecord invalid = FinancialRecord.builder().build();

		// Action and verification
		assertThrows(ConstraintViolationException.class, () -> service.save(buildUser(1L), invalid));
		verify(repository, Mockito.never()).save(invalid);
	}

	@Test
	public void updateFinancialRecord() {
		// Scenario
		FinancialRecord persistent = buildRecordWithIdAndUser();
		when(repository.save(Mockito.any(FinancialRecord.class))).thenReturn(persistent);
		when(repository.findById(persistent.getId())).thenReturn(Optional.of(persistent));

		// Action
		FinancialRecord record = service.update(persistent.getUser(), persistent.getId(), buildRecord());

		// Verification
		assertThat(record.getId()).isEqualTo(persistent.getId());
		verify(repository, Mockito.times(1)).save(persistent);
	}

	@Test
	public void testUpdateInvalidFinancialRecord() {
		// Scenario
		FinancialRecord invalid = FinancialRecord.builder().build();

		// Action and verification
		assertThrows(ConstraintViolationException.class, () -> service.update(buildUser(1L), 1L, invalid));
		verify(repository, Mockito.never()).save(invalid);
	}

	@Test
	public void testUpdateAndNotFoundFinancialRecord() {
		// Scenario
		FinancialRecord invalid = buildRecord();
		when(repository.findById(1L)).thenReturn(Optional.empty());

		// Action and verification
		assertThrows(FinancialRecordNotFoundException.class, () -> service.update(buildUser(1L), 1L, invalid));
		verify(repository, Mockito.never()).save(invalid);
	}
	
	@Test
	public void testUpdateFinancialRecordOfOtherUser() {
		// Scenario
		FinancialRecord record = buildRecordWithIdAndUser();
		when(repository.findById(record.getId())).thenReturn(Optional.of(record));
		
		// Action and verification
		assertThrows(BusinessAuthorizationException.class, () -> service.update(buildUser(2L), record.getId(), buildRecord()));
	}

	@Test
	public void testUpdateFinancialRecordStatusAndPersist() {
		// Scenario
		FinancialRecord record = buildRecordWithIdAndUser();
		when(repository.findById(record.getId())).thenReturn(Optional.of(record));
		
		// Action
		service.updateStatus(record.getUser(), record.getId(), FinancialRecordStatus.ACCEPTED);
		
		// Verification
		verify(repository, Mockito.times(1)).save(Mockito.any(FinancialRecord.class));
	}
	
	@Test
	public void testUpdateFinancialRecordStatusAndNotPersist() {
		// Scenario
		FinancialRecord record = buildRecordWithIdAndUser();
		when(repository.findById(1L)).thenReturn(Optional.of(record));
		
		// Action
		service.updateStatus(record.getUser(), record.getId(), record.getStatus());
		
		// Verification
		verify(repository, Mockito.times(0)).save(Mockito.any(FinancialRecord.class));
	}

	@Test
	public void testUpdateFinancialRecordStatusAndNotFound() {
		when(repository.findById(1L)).thenReturn(Optional.empty());

		// Action and verification
		assertThrows(FinancialRecordNotFoundException.class, () -> service.updateStatus(buildUser(1L), 1L, FinancialRecordStatus.CANCELLED));
	}

	@Test
	public void testUpdateFinancialRecordStatusOfOtherUser() {
		// Scenario
		FinancialRecord persistent = buildRecordWithIdAndUser();
		when(repository.findById(persistent.getId())).thenReturn(Optional.of(persistent));

		// Action and Verification
		assertThrows(BusinessAuthorizationException.class, () -> service.update(buildUser(2L), persistent.getId(), buildRecord()));
	}
	
	@Test
	public void testFindById() {
		// Scenario
		FinancialRecord persistent = buildRecordWithIdAndUser();
		when(repository.findById(persistent.getId())).thenReturn(Optional.of(persistent));
		
		// Action
		FinancialRecord record = service.findById(persistent.getUser(), persistent.getId());
		
		// Verification
		assertThat(record).isNotNull();
		assertThat(record.getId()).isEqualTo(persistent.getId());
	}
	
	@Test
	public void testFindByIdAndNotFound() {
		// Scenario
		when(repository.findById(1L)).thenReturn(Optional.empty());
		
		// Action and verification
		assertThrows(FinancialRecordNotFoundException.class, () -> service.findById(buildUser(1L), 1L));
	}
	
	@Test
	public void testFindByIdOfOtherUser() {
		// Scenario
		FinancialRecord persistent = buildRecordWithIdAndUser();
		when(repository.findById(persistent.getId())).thenReturn(Optional.of(persistent));
		
		// Action and verification
		assertThrows(BusinessAuthorizationException.class, () -> service.findById(buildUser(2L), persistent.getId()));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testFindByFilters() {
		// Scenario
		List<FinancialRecord> records = new ArrayList<>();
		records.add(buildRecordWithIdAndUser());
		Page<FinancialRecord> queryResult = new PageImpl<>(records, PageRequest.of(0, 100), 1);
		when(repository.findAll(Mockito.any(Example.class), Mockito.any(Pageable.class))).thenReturn(queryResult);
		
		// Action
		Page<FinancialRecord> page = service.find(1, 100, buildUser(1L), null, null, null, null);
		
		// Verification
		assertThat(page.isEmpty()).isFalse();
		assertThat(page.getTotalElements()).isEqualTo(1);
	}
	
	@Test
	public void testFindByFiltersWithInvalidPagination() {
		assertThrows(ConstraintViolationException.class, () -> service.find(0, 215, buildUser(1L), null, null, null, null));
	}

	@Test
	public void testDeleteFinancialRecord() {
		// Scenario
		FinancialRecord record = buildRecordWithIdAndUser();
		when(repository.findById(1L)).thenReturn(Optional.of(record));
		doNothing().when(repository).deleteById(1L);

		// Action and verification
		service.delete(buildUser(1L), 1L);
		verify(repository, Mockito.times(1)).deleteById(1L);
	}

	@Test
	public void testDeleteFinancialRecordAndNotFound() {
		when(repository.findById(1L)).thenReturn(Optional.empty());

		// Action and verification
		assertThrows(FinancialRecordNotFoundException.class, () -> service.delete(buildUser(1L), 1L));
	}

	@Test
	public void testDeleteFinancialRecordOfOtherUser() {
		// Scenario
		FinancialRecord record = buildRecordWithIdAndUser();
		when(repository.findById(1L)).thenReturn(Optional.of(record));

		// Action and verification
		assertThrows(BusinessAuthorizationException.class, () -> service.delete(buildUser(2L), 1L));
	}
	
	private User buildUser(Long id) {
		return User.builder()
			.id(id)
			.build();
	}
	
	private FinancialRecord buildRecordWithIdAndUser() {
		FinancialRecord record = buildRecord();
		record.setId(1L);
		record.setUser(buildUser(1L));
		return record;
	}

	private FinancialRecord buildRecord() {
		return FinancialRecord.builder()
			.year(2020)
			.month(9)
			.description("Registro financeiro qualquer")
			.value(999F)
			.type(FinancialRecordType.INCOME)
			.status(FinancialRecordStatus.PENDANT)
			.registerDate(LocalDate.now())
			.build();
	}
}
