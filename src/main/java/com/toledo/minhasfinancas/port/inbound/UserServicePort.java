package com.toledo.minhasfinancas.port.inbound;

import javax.validation.Valid;

import com.toledo.minhasfinancas.domain.User;

public interface UserServicePort {
	
	User authenticate(String email, String password);
	
	User register(@Valid User toSave);
	
	User findById(long id, String email);
	
	User findByEmail(String email);
	
	void validateEmail(String email);
}
