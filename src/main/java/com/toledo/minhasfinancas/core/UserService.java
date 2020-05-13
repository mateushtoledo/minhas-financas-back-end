package com.toledo.minhasfinancas.core;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.toledo.minhasfinancas.domain.User;
import com.toledo.minhasfinancas.exception.custom.AuthenticationFailureException;
import com.toledo.minhasfinancas.exception.custom.BusinessRuleException;
import com.toledo.minhasfinancas.port.inbound.UserServicePort;
import com.toledo.minhasfinancas.repository.UserRepository;

@Service
public class UserService implements UserServicePort {
	private UserRepository repository;
	
	@Autowired
	public UserService(UserRepository repository) {
		super();
		this.repository = repository;
	}

	@Override
	public User authenticate(String email, String password) {
		Optional<User> user = repository.findByEmail(email);
		if (!user.isPresent()) {
			throw new AuthenticationFailureException("Usuário não encontrado para o e-mail informado.");
		}
		
		User found = user.get();
		if (!found.getPassword().equals(password)) {
			throw new AuthenticationFailureException("Senha inválida.");
		}
		return found;
	}

	@Override
	@Transactional
	public User register(User toSave) {
		validateEmail(toSave.getEmail());
		return repository.save(toSave);
	}

	@Override
	public void validateEmail(String email) {
		if (repository.existsByEmail(email)) {
			throw new BusinessRuleException("Já existe um usuário cadastrado com este e-mail.");
		}
	}
}
