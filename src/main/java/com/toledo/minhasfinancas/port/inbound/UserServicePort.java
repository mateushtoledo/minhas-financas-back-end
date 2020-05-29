package com.toledo.minhasfinancas.port.inbound;

import com.toledo.minhasfinancas.domain.User;

public interface UserServicePort {
	
	User authenticate(String email, String password);
	
	User register(User toSave);
	
	User findById(long id, String email);
	
	void validateEmail(String email);
}
