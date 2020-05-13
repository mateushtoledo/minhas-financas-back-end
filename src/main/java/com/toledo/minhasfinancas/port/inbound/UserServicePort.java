package com.toledo.minhasfinancas.port.inbound;

import com.toledo.minhasfinancas.domain.User;

public interface UserServicePort {
	
	User authenticate(String email, String password);
	
	User register(User toSave);
	
	void validateEmail(String email);
}
