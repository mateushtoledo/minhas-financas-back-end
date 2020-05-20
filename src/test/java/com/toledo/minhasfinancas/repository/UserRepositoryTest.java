package com.toledo.minhasfinancas.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.toledo.minhasfinancas.domain.User;

@DataJpaTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UserRepositoryTest {
	@Autowired
	private UserRepository repository;
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testUserExistsByEmail() {
		// Scenario
		User roberval = createUser();
		entityManager.persist(roberval);
				
		// action
		boolean robervalExists = repository.existsByEmail(roberval.getEmail());
		
		// verification
		assertThat(robervalExists).isTrue();
	}
	
	@Test
	public void testUserNotExistsByEmail() {
		// action
		boolean robervalExists = repository.existsByEmail("mateushtoledo@gmail.com");
		
		// verification
		assertThat(robervalExists).isFalse();
	}
	
	@Test
	public void testToSaveUser() {
		// scenario
		User roberval = createUser();
		
		// action
		User saved = repository.save(roberval);
		
		// Verification
		assertThat(saved.getId()).isNotNull();
	}
	
	@Test
	public void testFindUserByEmail() {
		// Scenario
		User roberval = createUser();
		entityManager.persist(roberval);
		
		// Action
		Optional<User> found = repository.findByEmail("roberval@gmail.com");
		
		// Verification
		assertTrue(found.isPresent());
	}
	
	@Test
	public void testNotFoundUserByEmail() {
		// Action
		Optional<User> found = repository.findByEmail("roberval@gmail.com");
		
		// Verification
		assertFalse(found.isPresent());
	}
	
	public static User createUser() {
		return User.builder()
			.name("Roberval Da Silva")
			.email("roberval@gmail.com")
			.password("senha")
			.build();
	}
}
