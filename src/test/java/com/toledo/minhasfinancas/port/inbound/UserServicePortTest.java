package com.toledo.minhasfinancas.port.inbound;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.toledo.minhasfinancas.core.UserService;
import com.toledo.minhasfinancas.domain.User;
import com.toledo.minhasfinancas.exception.custom.AuthenticationFailureException;
import com.toledo.minhasfinancas.exception.custom.BusinessRuleException;
import com.toledo.minhasfinancas.repository.UserRepository;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class UserServicePortTest {
    private UserServicePort service;
    @MockBean
    private UserRepository repository;
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @BeforeEach
    public void setUp() {
    	service = new UserService(repository, passwordEncoder);
    }
    
    @Test
    public void testSuccessfulAuthentication() {
    	// Scenario
    	String email = "email@email.com";
    	String password = "password";
    	User user = User.builder().email(email).password(passwordEncoder.encode(password)).build();
    	when(repository.findByEmail(email)).thenReturn(Optional.of(user));
    	
    	// Action
    	User authenticated = service.authenticate(email, password);
    	
    	// Verification
    	assertThat(authenticated).isNotNull();
    }
    
    @Test
    public void testAuthenticationAndNotFoundUser() {
    	when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
    	
    	// Action and verification
    	assertThrows(AuthenticationFailureException.class, () -> service.authenticate("email@email.com", "teste124"));
    }
    
    @Test
    public void testAuthenticationAndPasswordNotMatch() {
    	String password = "Pwd1234";
    	User user = User.builder().email("email@email.com").password(password).build();
    	when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(user));
    	
    	// Action and verification
    	assertThrows(AuthenticationFailureException.class, () -> service.authenticate("email@email.com", "pwd1234"));
    }
    
    @Test
    public void testValidateValidEmail() {
        // scenario
        when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);
        
        // Action
        service.validateEmail("mateushtoledo@gmail.com");
    }
    
    @Test
    public void testValidateInvalidEmail() {
    	// scenario
    	when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
        
    	// Action
    	Assertions.assertThrows(BusinessRuleException.class, () -> service.validateEmail("mateushtoledo@gmail.com"));
    }
}
