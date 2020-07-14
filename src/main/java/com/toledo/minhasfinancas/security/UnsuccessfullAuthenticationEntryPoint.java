package com.toledo.minhasfinancas.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toledo.minhasfinancas.exception.ErrorResponse;

public class UnsuccessfullAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
		ObjectMapper mapper = new ObjectMapper();
		ErrorResponse errorResponse = new ErrorResponse("Credenciais de acesso inválidas. Por favor, verifique os dados informados, e tente novamente.");

		// Define response status and body
		response.getWriter().write(mapper.writeValueAsString(errorResponse));
		response.addHeader("content-type", "application/json");
		response.setStatus(HttpStatus.UNAUTHORIZED.value());

	}

}
