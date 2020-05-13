package com.toledo.minhasfinancas.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.toledo.minhasfinancas.domain.User;

@DataJpaTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UserRepositoryTest {
	@Autowired
	UserRepository repository;
	
	@Test
	public void testUserExistsByEmail() {
		// Scenario
		User roberval = User.builder()
			.name("Roberval Da Silva")
			.email("roberval@gmail.com")
			.build();
		repository.save(roberval);
				
		// action
		boolean robervalExists = repository.existsByEmail(roberval.getEmail());
		
		// verification
		assertThat(robervalExists).isTrue();
	}
	
	@Test
	public void testUserNotExistsByEmail() {
		// Scenario
		repository.deleteAll();
				
		// action
		boolean robervalExists = repository.existsByEmail("mateushtoledo@gmail.com");
		
		// verification
		assertThat(robervalExists).isFalse();
	}
}
