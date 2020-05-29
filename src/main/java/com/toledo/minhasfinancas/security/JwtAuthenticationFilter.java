package com.toledo.minhasfinancas.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toledo.minhasfinancas.dto.AuthenticationDTO;
import com.toledo.minhasfinancas.dto.CredentialsDTO;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private JwtUtil jwtUtils;
    private AuthenticationManager authManager;
	private Long expiration;
	
    @Autowired
    public JwtAuthenticationFilter(AuthenticationManager authManager, Environment env, JwtUtil jwtUtils) {
        this.jwtUtils = jwtUtils;
        this.authManager = authManager;
        this.expiration = Long.parseLong(env.getProperty("security.jwt.expiration"));
        
        // Change login URL
        this.setFilterProcessesUrl("/auth/authentications");
    }

    /**
     * Process the authentication request (POST /auth/authentications).
     *
     * @param request Current request;
     * @param response Current response;
     *
     * @return Authentication.
     *
     * @throws AuthenticationException :/
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            // Try to parse the request credentials (request body)
            CredentialsDTO credentials = new ObjectMapper().readValue(request.getInputStream(), CredentialsDTO.class);
            
            // Try to create spring authentication token
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(credentials.getEmail(), credentials.getPassword());
            
            // Use the created authentication manager to authenticate
            Authentication auth = authManager.authenticate(authToken);
            
            // Return the authentication
            return auth;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method that defines the steps after the successful authentication.
     * 
     * @param request Current request.
     * @param response The request response.
     * @param chain Filter chain.
     * @param auth The authentication created on attemptAuthentication method.
     * 
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication auth
    ) throws IOException, ServletException {
        String email = ((User) auth.getPrincipal()).getUsername();
        String token = jwtUtils.generateToken(email);
        response.setStatus(HttpStatus.CREATED.value());
        response.addHeader("Content-Type", "application/json");
        response.getWriter().write(getSucessfullAuthenticationData(token));
    }
    
    /**
     * Create the successfully authentication response body data.
     * 
     * @param jwt User authorization token.
     * 
     * @return Successfully authentication response body.
     */
    private String getSucessfullAuthenticationData(String jwt) {
    	try {
    		AuthenticationDTO data = new AuthenticationDTO(jwt, expiration/1000);
        	ObjectMapper objectMapper = new ObjectMapper();
        	return objectMapper.writeValueAsString(data);
		} catch (Exception e) {
			e.printStackTrace();
			return "{\"jwt\":\"\", \"expiresIn\": 0}";
		}
    }
}