package com.toledo.minhasfinancas.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.toledo.minhasfinancas.domain.User;
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
		return null;
	}

	@Override
	public User register(User toSave) {
		return null;
	}

	@Override
	public void validateEmail(String email) {
		if (repository.existsByEmail(email)) {
			throw new BusinessRuleException("Já existe um usuário cadastrado com este e-mail.");
		}
	}
}
