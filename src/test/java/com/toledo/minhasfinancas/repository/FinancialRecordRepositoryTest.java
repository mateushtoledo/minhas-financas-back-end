package com.toledo.minhasfinancas.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.toledo.minhasfinancas.domain.FinancialRecord;
import com.toledo.minhasfinancas.domain.User;
import com.toledo.minhasfinancas.domain.enums.FinancialRecordStatus;
import com.toledo.minhasfinancas.domain.enums.FinancialRecordType;

@DataJpaTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class FinancialRecordRepositoryTest {
	@Autowired
	private FinancialRecordRepository repository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private TestEntityManager entityManager;
	private User u = buildUser();
	
	@BeforeEach
	public void setUp() {
		u = userRepository.save(u);
	}
	
	@Test
	public void testSave() {
		// Scenario
		FinancialRecord record = buildRecord();
		
		// Action
		record = repository.save(record);
		
		// Verification
		assertThat(record.getId()).isNotNull();
	}
	
	@Test
	public void testUpdate() {
		// Scenario
		FinancialRecord record = createAndSaveFinancialRecord();
		
		// Action
		record.setYear(2018);
		record.setMonth(2);
		record.setDescription("Nova!");
		record.setStatus(FinancialRecordStatus.ACCEPTED);
		repository.save(record);
		
		// Verification
		FinancialRecord updated = entityManager.find(FinancialRecord.class, record.getId());
		assertThat(updated.getYear()).isEqualTo(2018);
		assertThat(updated.getMonth()).isEqualTo(2);
		assertThat(updated.getDescription()).isEqualTo("Nova!");
		assertThat(updated.getStatus()).isEqualTo(FinancialRecordStatus.ACCEPTED);
	}
	
	@Test
	public void testFindById() {
		// Scenario
		FinancialRecord record = createAndSaveFinancialRecord();
		
		// Action
		Optional<FinancialRecord> found = repository.findById(record.getId());
		
		// Verification
		assertThat(found.isPresent()).isTrue();
	}
	
	@Test
	public void testFindAllWithFiltersAndPagination() {
		// Scenario
		FinancialRecord record = createAndSaveFinancialRecord();
		FinancialRecord filters = new FinancialRecord(null, record.getDescription(), record.getMonth(), record.getYear(), null, record.getType(), null, record.getUser(), null);
		Example<FinancialRecord> ex = Example.of(filters,
			ExampleMatcher.matching()
			.withIgnoreCase()
			.withStringMatcher(StringMatcher.CONTAINING)
		);
		Pageable pagination = PageRequest.of(0, 100, Sort.by(Direction.DESC, "year", "month"));
		
		// Action
		Page<FinancialRecord> page = repository.findAll(ex, pagination);
		
		// Verification
		assertThat(page.isEmpty()).isFalse();
		assertThat(page.getNumberOfElements()).isEqualTo(1);
	}
	
	@Test
	public void testDeleteFinancialRecord() {
		// Scenario
		FinancialRecord record = createAndSaveFinancialRecord();
		
		// Action
		repository.deleteById(record.getId());
		
		// Verification
		FinancialRecord nonexistent = entityManager.find(FinancialRecord.class, record.getId());
		assertThat(nonexistent).isNull();
	}

	private FinancialRecord createAndSaveFinancialRecord() {
		FinancialRecord record = buildRecord();
		record = entityManager.persist(record);
		return record;
	}
	
	private FinancialRecord buildRecord() {
		return FinancialRecord.builder()
			.year(2020)
			.user(u)
			.month(9)
			.description("Registro financeiro qualquer")
			.value(999F)
			.type(FinancialRecordType.INCOME)
			.status(FinancialRecordStatus.PENDANT)
			.registerDate(LocalDate.now())
			.build();
	}
	
	private static User buildUser() {
		return User.builder()
			.name("Mateus Toledo")
			.email("mateus@email.com")
			.password("teste")
			.build();
	}
}

