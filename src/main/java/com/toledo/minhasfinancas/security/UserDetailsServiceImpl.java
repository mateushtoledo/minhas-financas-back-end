package com.toledo.minhasfinancas.security;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.toledo.minhasfinancas.domain.User;
import com.toledo.minhasfinancas.repository.UserRepository;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {
	private UserRepository repository;
	
	@Autowired
    public UserDetailsServiceImpl(UserRepository repository) {
        this.repository = repository;
    }
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// Validate email address (username)
        if (username == null || username.trim().isEmpty()) {
            throw new UsernameNotFoundException("Por favor, informe o e-mail da sua conta!");
        }
        
        // Load user from repository
		Optional<User> found = repository.findByEmail(username);
		if (found.isPresent()) {
			User user = found.get();
			return new org.springframework.security.core.userdetails.User(username, user.getPassword(), new ArrayList<>());
		}
		
		throw new UsernameNotFoundException("Não foi encontrada nenhuma conta com o e-mail informado!");
	}

}
