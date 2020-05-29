package com.toledo.minhasfinancas.core;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.toledo.minhasfinancas.domain.User;
import com.toledo.minhasfinancas.exception.UserNotFoundException;
import com.toledo.minhasfinancas.exception.custom.AuthenticationFailureException;
import com.toledo.minhasfinancas.exception.custom.BusinessAuthorizationException;
import com.toledo.minhasfinancas.exception.custom.BusinessRuleException;
import com.toledo.minhasfinancas.port.inbound.UserServicePort;
import com.toledo.minhasfinancas.repository.UserRepository;

@Service
public class UserService implements UserServicePort {
	private UserRepository repository;
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	public UserService(UserRepository repository, BCryptPasswordEncoder passwordEncoder) {
		super();
		this.repository = repository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public User authenticate(String email, String password) {
		Optional<User> user = repository.findByEmail(email);
		if (!user.isPresent()) {
			throw new AuthenticationFailureException("Usuário não encontrado para o e-mail informado.");
		}
		
		User found = user.get();
		if (!passwordEncoder.matches(password, found.getPassword())) {
			throw new AuthenticationFailureException("Senha inválida.");
		}
		return found;
	}

	@Override
	@Transactional
	public User register(User toSave) {
		validateEmail(toSave.getEmail());
		toSave.setPassword(passwordEncoder.encode(toSave.getPassword()));
		toSave.setRegisterDate(LocalDate.now());
		return repository.save(toSave);
	}

	@Override
	public void validateEmail(String email) {
		if (repository.existsByEmail(email)) {
			throw new BusinessRuleException("Já existe um usuário cadastrado com este e-mail.");
		}
	}

	@Override
	public User findById(long id, String email) {
		Optional<User> found = repository.findById(id);
		
		if (!found.isPresent()) {
			throw new UserNotFoundException("Usuário não encontrado!");
		}
		
		User user = found.get();
		if (user.getEmail().equals(email)) {
			return user;
		}
		throw new BusinessAuthorizationException("Você não pode acessar os dados de outro usuário!");
	}
}
