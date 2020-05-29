package com.toledo.minhasfinancas.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toledo.minhasfinancas.exception.ErrorResponse;
import com.toledo.minhasfinancas.exception.custom.AuthenticationFailureException;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
	private JwtUtil jwtUtil;
	private UserDetailsService userDetailsService;

	public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserDetailsService userDetailsService, JwtUtil jwtUtil, Environment env) {
		super(authenticationManager);
		this.userDetailsService = userDetailsService;
		this.jwtUtil = jwtUtil;
	}

	/**
	 * This method is executed before the request. In this application, this method
	 * is responsible by the authorization filter.
	 * 
	 * @param request  Current request.
	 * @param response The request response.
	 * @param chain    Filter chain.
	 * 
	 * @throws java.io.IOException
	 * @throws javax.servlet.ServletException
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		// Validate authorization header (Bearer + " " + Jwt)
		String authHeader = request.getHeader("Authorization");
		boolean proceed = true;
		if (authHeader != null && !authHeader.trim().isEmpty() && authHeader.startsWith("Bearer ")) {
			// Set request authentication
			try {
				UsernamePasswordAuthenticationToken authToken = getAuthenticationToken(authHeader.substring(7));
				SecurityContextHolder.getContext().setAuthentication(authToken);
			} catch (AuthenticationException ae) {
				proceed = false;
				responseAsUnauthorized(ae, response);
			}
		}
		
		// To continue processing the request
		if (proceed) {
			chain.doFilter(request, response);
		}
	}

	/**
	 * Validate the jwt and create a internal token with user authentication
	 * details.
	 * 
	 * @param jwt Jwt token, obtained from request headers.
	 * 
	 * @return Token that defines the user authentication.
	 */
	private UsernamePasswordAuthenticationToken getAuthenticationToken(String jwt) throws AuthenticationFailureException {
		// Validate Jwt
		jwtUtil.validateJwt(jwt);
		
		// Load user from database
		UserDetails user = userDetailsService.loadUserByUsername(jwtUtil.getUserEmail(jwt));
		
		// Define user authentication: current user, authorizations, authorities
		return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
	}
	
	private void responseAsUnauthorized(AuthenticationException exception, HttpServletResponse response) {
		// custom error response class used across my project
		ObjectMapper mapper = new ObjectMapper();

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setHeader("Content-type", "application/json");
        try {
        	ErrorResponse errorResponse = new ErrorResponse(exception.getMessage());
        	response.getWriter().write(mapper.writeValueAsString(errorResponse));
		} catch (Exception e) {
			// Nothing to do
		}
	}
}
