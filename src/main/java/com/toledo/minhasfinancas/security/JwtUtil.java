package com.toledo.minhasfinancas.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.toledo.minhasfinancas.exception.custom.AuthenticationFailureException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;

@Component
public class JwtUtil {

	private String secret;
	private Long expiration;

	@Autowired
	public JwtUtil(Environment env) {
		this.secret = env.getProperty("security.jwt.secret");
		this.expiration = Long.parseLong(env.getProperty("security.jwt.expiration"));
	}

	public String generateToken(String username) {
		// Create signing key
		return Jwts.builder().setSubject(username).setExpiration(new Date(System.currentTimeMillis() + expiration))
				.signWith(SignatureAlgorithm.HS512, secret).compact();
	}

	public void validateJwt(String jwt) throws AuthenticationFailureException {
		try {
			// Parse Jwt with secret and return the user claims
			Jwts.parser().setSigningKey(secret).parseClaimsJws(jwt).getBody();
		} catch (ExpiredJwtException eje) {
			throw new AuthenticationFailureException("Seu token de autenticação expirou!");
		} catch (MalformedJwtException | UnsupportedJwtException | SignatureException | IllegalArgumentException ex) {
			throw new AuthenticationFailureException("Seu token de autenticação é inválido!");
		}
	}

	private Claims getClaims(String token) {
		try {
			// Parse Jwt with secret and return the user claims
			return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
		} catch (ExpiredJwtException | MalformedJwtException | UnsupportedJwtException | SignatureException | IllegalArgumentException e) {
			return null;
		}
	}

	public String getUserEmail(String token) {
		Claims claims = getClaims(token);
		if (claims != null) {
			return claims.getSubject();
		}
		return null;
	}
}
