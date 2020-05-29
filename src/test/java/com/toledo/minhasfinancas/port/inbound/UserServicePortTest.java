package com.toledo.minhasfinancas.port.inbound;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.toledo.minhasfinancas.core.UserService;
import com.toledo.minhasfinancas.domain.User;
import com.toledo.minhasfinancas.exception.UserNotFoundException;
import com.toledo.minhasfinancas.exception.custom.AuthenticationFailureException;
import com.toledo.minhasfinancas.exception.custom.BusinessAuthorizationException;
import com.toledo.minhasfinancas.exception.custom.BusinessRuleException;
import com.toledo.minhasfinancas.repository.UserRepository;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class UserServicePortTest {
	@SpyBean
    private UserService service;
    @MockBean
    private UserRepository repository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Test
    public void testFindUserById() {
    	// Scenario
    	String userEmail = "email@email.com";
    	User toFind = new User(1L, "mateus", userEmail, "pweovg4igk09f0weu", LocalDate.now(), new ArrayList<>());
    	when(repository.findById(Mockito.anyLong())).thenReturn(Optional.of(toFind));
    	
    	// Action
    	User found = service.findById(1L, userEmail);
    	
    	// Verification
    	assertThat(found).isNotNull();
    	assertThat(found.getId()).isNotNull();
    }
    
    @Test
    public void testFindUserByIdAndNotFound() {
    	// Scenario
    	when(repository.findById(Mockito.anyLong())).thenThrow(UserNotFoundException.class);
    	
    	// Action and verification
    	assertThrows(UserNotFoundException.class, () -> service.findById(1L, "email@email.com"));
    }
    
    @Test
    public void testFindUnauthorizedUserById() {
    	// Scenario
    	String userEmail = "email@email.com";
    	User toFind = new User(1L, "mateus", userEmail, "pweovg4igk09f0weu", LocalDate.now(), new ArrayList<>());
    	when(repository.findById(Mockito.anyLong())).thenReturn(Optional.of(toFind));
    	
    	// Action
    	assertThrows(BusinessAuthorizationException.class, () -> service.findById(1L, userEmail.concat(".br")));
    }
    
    @Test
    public void testSaveUser() {
    	// Scenario
    	doNothing().when(service).validateEmail(Mockito.anyString());
    	User user = User.builder()
			.id(1L)
			.name("Roberval")
			.email("email@email.com")
			.password(passwordEncoder.encode("password"))
			.build();
    	Mockito.when(repository.save(Mockito.any(User.class))).thenReturn(user);
    	
    	// Action
    	User saved = service.register(User.builder().password("password").build());
    	
    	// Verification
    	assertThat(saved).isNotNull();
    	assertThat(saved.getId()).isEqualTo(1L);
    	assertThat(saved.getName()).isEqualTo("Roberval");
    	assertThat(saved.getEmail()).isEqualTo("email@email.com");
    	assertThat(passwordEncoder.matches("password", saved.getPassword())).isTrue();
    }
    
    @Test
    public void testSaveUserWithInvalidEmail() {
    	// Scenario
    	String email = "email@email.com";
    	User user = User.builder().email(email).build();
    	doThrow(BusinessRuleException.class).when(service).validateEmail(email);
    	
    	// Action
    	assertThrows(BusinessRuleException.class, () -> service.register(user));
    	verify(repository, Mockito.never()).save(user);
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
    	// Scenario
    	when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
    	
    	// Action
    	Throwable exception = catchThrowable(() -> service.authenticate("email@email.com", "teste124"));
    	
    	// Verification
    	assertThat(exception).isInstanceOf(AuthenticationFailureException.class);
    	assertThat(exception.getMessage()).isEqualTo("Usuário não encontrado para o e-mail informado.");
    }
    
    @Test
    public void testAuthenticationAndPasswordNotMatch() {
    	// Scenario
    	String password = "Pwd1234";
    	User user = User.builder().email("email@email.com").password(password).build();
    	when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(user));
    	
    	// Action
    	Throwable exception = catchThrowable(() -> service.authenticate("email@email.com", "pwd1234"));
    	
    	// Verification
    	assertThat(exception).isInstanceOf(AuthenticationFailureException.class);
    	assertThat(exception.getMessage()).isEqualTo("Senha inválida.");
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
