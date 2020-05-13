package com.toledo.minhasfinancas.port.inbound;

import com.toledo.minhasfinancas.domain.User;
import com.toledo.minhasfinancas.exception.custom.BusinessRuleException;
import com.toledo.minhasfinancas.repository.UserRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class UserServicePortTest {
    @Autowired
    private UserServicePort service;
    @Autowired
    private UserRepository repository;
    
    @Test
    public void testValidateValidEmail() {
        // scenario
        repository.deleteAll();
        // Action
        service.validateEmail("mateushtoledo@gmail.com");
    }
    
    @Test
    public void testValidateInvalidEmail() {
        User mateus = User.builder()
            .name("Mateus Toledo")
            .email("mateushtoledo@gmail.com")
            .password("mateus123")
            .registerDate(LocalDate.now())
            .build();
        repository.save(mateus);
        
        Assertions.assertThrows(BusinessRuleException.class, () -> service.validateEmail(mateus.getEmail()));
    }
}
