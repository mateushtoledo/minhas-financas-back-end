package com.toledo.minhasfinancas.adapter.inbound;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.toledo.minhasfinancas.dto.AuthenticationDTO;
import com.toledo.minhasfinancas.security.JwtUtil;

@RestController
@RequestMapping("/auth/refreshs")
public class AuthenticationRefreshRestAdapter {
	
	private JwtUtil jwtUtil;
	private Long expiration;
	
	@Autowired
	private AuthenticationRefreshRestAdapter(Environment env) {
		this.jwtUtil = new JwtUtil(env);
		this.expiration = Long.parseLong(env.getProperty("security.jwt.expiration"));
	}
	
	@PostMapping
	public ResponseEntity<AuthenticationDTO> refreshAuthenticationToken(@RequestHeader("authorization") String authorization) {
		String actualJwt = authorization.substring(7);
		String userEmail = jwtUtil.getUserEmail(actualJwt);
		String newJwt = jwtUtil.generateToken(userEmail);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(new AuthenticationDTO(newJwt, expiration));
	}
}
