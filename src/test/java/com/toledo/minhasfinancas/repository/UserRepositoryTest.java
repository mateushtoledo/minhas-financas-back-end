package com.toledo.minhasfinancas.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.toledo.minhasfinancas.domain.User;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
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
}
