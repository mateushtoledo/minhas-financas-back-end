package com.toledo.minhasfinancas.adapter.inbound;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.toledo.minhasfinancas.domain.User;
import com.toledo.minhasfinancas.dto.UserDTO;
import com.toledo.minhasfinancas.port.inbound.UserServicePort;
import com.toledo.minhasfinancas.security.JwtUtil;

@RestController
@RequestMapping("/users")
public class UserRestAdapter {
	private UserServicePort service;
	private JwtUtil jwtUtils;
	
	@Autowired
	public UserRestAdapter(UserServicePort service, JwtUtil jwtUtils) {
		super();
		this.service = service;
		this.jwtUtils = jwtUtils;
	}

	@PostMapping
	public ResponseEntity<Void> registerUser(@RequestBody UserDTO userData) {
		User saved = service.register(userData.toUser());
		URI userLocation = ServletUriComponentsBuilder.fromCurrentRequest()
			.path("/{id}")
			.buildAndExpand(saved.getId())
			.toUri();
		
		return ResponseEntity.created(userLocation).build();
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<UserDTO> findUser(@PathVariable(name = "id") Long id, @RequestHeader("authorization") String authorization) {
		String jwt = authorization.substring(7);
		String userEmail = jwtUtils.getUserEmail(jwt);
		
		User found = service.findById(id, userEmail);
		
		UserDTO dto = new UserDTO(found);
		return ResponseEntity.ok(dto);
	}
}
